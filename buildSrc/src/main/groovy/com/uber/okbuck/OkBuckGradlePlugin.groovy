package com.uber.okbuck

import com.uber.okbuck.core.dependency.DependencyCache
import org.gradle.api.artifacts.ResolvedDependency
import com.uber.okbuck.core.dependency.ExternalDependency
import com.uber.okbuck.core.model.base.ProjectType
import com.uber.okbuck.core.model.base.TargetCache
import com.uber.okbuck.core.model.java.JavaTarget
import com.uber.okbuck.core.task.OkBuckCleanTask
import com.uber.okbuck.core.util.FileUtil
import com.uber.okbuck.core.util.GroovyUtil
import com.uber.okbuck.core.util.ProguardUtil
import com.uber.okbuck.core.util.ProjectUtil
import com.uber.okbuck.core.util.RetrolambdaUtil
import com.uber.okbuck.core.util.RobolectricUtil
import com.uber.okbuck.core.util.TransformUtil
import com.uber.okbuck.core.util.ProjectUtil.BuildSystem
import com.uber.okbuck.extension.ExperimentalExtension
import com.uber.okbuck.extension.IntellijExtension
import com.uber.okbuck.extension.OkBuckExtension
import com.uber.okbuck.extension.RetrolambdaExtension
import com.uber.okbuck.extension.TestExtension
import com.uber.okbuck.extension.TransformExtension
import com.uber.okbuck.extension.WrapperExtension
import com.uber.okbuck.generator.BuckFileGenerator
import com.criteo.tobazel.generator.BazelFileGenerator
import com.criteo.tobazel.core.util.AddstattoolUtil
import com.criteo.tobazel.core.util.JunitTestsUtil
import com.uber.okbuck.generator.DotBuckConfigLocalGenerator
import com.uber.okbuck.wrapper.BuckWrapperTask
import org.apache.commons.io.IOUtils
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.repositories.ArtifactRepository
import org.gradle.api.artifacts.repositories.FlatDirectoryArtifactRepository
import org.gradle.api.artifacts.repositories.IvyArtifactRepository
import org.gradle.api.artifacts.repositories.MavenArtifactRepository

class OkBuckGradlePlugin implements Plugin<Project> {

    public static final String EXTERNAL_DEP_BUCK_FILE = "thirdparty/BUCK_FILE"
    public static final String OKBUCK = "okbuck"
    public static final String OKBUCK_CLEAN = 'okbuckClean'
    public static final String BUCK = "BUCK"
    public static final String EXPERIMENTAL = "experimental"
    public static final String INTELLIJ = "intellij"
    public static final String TEST = "test"
    public static final String WRAPPER = "wrapper"
    public static final String BUCK_WRAPPER = "buckWrapper"
    public static final String DEFAULT_CACHE_PATH = ".okbuck/cache"
    public static final String GROUP = "okbuck"
    public static final String BUCK_LINT = "buckLint"
    public static final String BUCK_LINT_LIBRARY = "buckLintLibrary"
    public static final String LINT = "lint"
    public static final String TRANSFORM = "transform"
    public static final String RETROLAMBDA = "retrolambda"
    public static final String CONFIGURATION_EXTERNAL = "externalOkbuck"
    public static final String OKBUCK_DEFS = ".okbuck/defs/DEFS"

    public static final String WORKSPACE = "WORKSPACE"
    public static final String BUILD = "BUILD"
    public static final String INT_LIBS = "intlibs"
    public static final String EXT_LIBS = "extlibs"
    public static final String JAR = "jar"

    // TODO: configure that in the plugin
    public static final String INTERNAL_PROJECTS_PREFIX = "com.criteo"
    public static final String INTERNAL_NEXUS = "http://nexus.criteo.prod/content/groups/criteodev"

    // Project level globals
    public DependencyCache depCache
    public DependencyCache lintDepCache
    public Map <BuildSystem, TargetCache> targetCache
    public String retrolambdaCmd

    void apply(Project project) {
        // Create extensions
        OkBuckExtension okbuckExt = project.extensions.create(OKBUCK, OkBuckExtension, project)
        WrapperExtension wrapper = okbuckExt.extensions.create(WRAPPER, WrapperExtension)
        ExperimentalExtension experimental = okbuckExt.extensions.create(EXPERIMENTAL, ExperimentalExtension)
        TestExtension test = okbuckExt.extensions.create(TEST, TestExtension)
        IntellijExtension intellij = okbuckExt.extensions.create(INTELLIJ, IntellijExtension)
        RetrolambdaExtension retrolambda = okbuckExt.extensions.create(RETROLAMBDA, RetrolambdaExtension)
        okbuckExt.extensions.create(TRANSFORM, TransformExtension)

        // Create configurations
        project.configurations.maybeCreate(TransformUtil.CONFIGURATION_TRANSFORM)
        Configuration externalOkbuck = project.configurations.maybeCreate(CONFIGURATION_EXTERNAL)

        // Create tasks
        Task setupOkbuck = project.task('setupOkbuck')
        setupOkbuck.setGroup(GROUP)
        setupOkbuck.setDescription("Setup okbuck cache and dependencies")

        Task okBuck = project.task(OKBUCK)
        okBuck.setGroup(GROUP)
        okBuck.setDescription("Generate BUCK files")
        okBuck.dependsOn(setupOkbuck)

        // Create target cache
        targetCache = [(ProjectUtil.BuildSystem.BUCK): new TargetCache(),
                       (ProjectUtil.BuildSystem.BAZEL): new com.criteo.tobazel.core.model.base.TargetCache()]

        project.afterEvaluate {
            // Create clean task
            Task okBuckClean = project.tasks.create(OKBUCK_CLEAN, OkBuckCleanTask, {
                dir = project.projectDir.absolutePath
                includes = wrapper.remove
                excludes = wrapper.keep
            })
            okBuckClean.setGroup(GROUP)
            okBuckClean.setDescription("Delete configuration files generated by OkBuck")

            // Create wrapper task
            BuckWrapperTask buckWrapper = project.tasks.create(BUCK_WRAPPER, BuckWrapperTask, {
                repo = wrapper.repo
                remove = wrapper.remove
                keep = wrapper.keep
                watch = wrapper.watch
                sourceRoots = wrapper.sourceRoots
            })
            buckWrapper.setGroup(GROUP)
            buckWrapper.setDescription("Create buck wrapper")

            // Configure setup task
            setupOkbuck.dependsOn(okBuckClean)
            setupOkbuck.doLast {
                addSubProjectRepos(project as Project, okbuckExt.buckProjects as Set<Project>)
                Set<Configuration> projectConfigurations = configurations(okbuckExt.buckProjects)
                projectConfigurations.addAll([externalOkbuck])

                depCache = new DependencyCache(
                        "external",
                        project,
                        DEFAULT_CACHE_PATH,
                        projectConfigurations,
                        EXTERNAL_DEP_BUCK_FILE,
                        true,
                        true,
                        intellij.sources,
                        false, // false = !lint.disabled, lint.disabled = true,
                        okbuckExt.buckProjects,
                        INTERNAL_PROJECTS_PREFIX)

                // Fetch transform deps if needed
                if (experimental.transform) {
                    TransformUtil.fetchTransformDeps(project)
                }

                // Fetch Retrolambda deps if needed
                boolean hasRetrolambda = okbuckExt.buckProjects.find {
                    it.plugins.hasPlugin('me.tatarka.retrolambda')
                } != null
                if (hasRetrolambda) {
                    RetrolambdaUtil.fetchRetrolambdaDeps(project, retrolambda)
                }

                // Fetch robolectric deps if needed
                if (test.robolectric) {
                    RobolectricUtil.download(project)
                }
                // Generate Workspace
                generateWorkspace(project)

                // Generate intlibs extlibs
                generateIntAndExtLibs(project)

                // Generate additional bazel rules
                generateBzlRules(project)

                // Generate BUILD
                okbuckExt.buckProjects.each {
                    Project p ->
                        BazelFileGenerator.generate(p)
                }
            }

            // Configure okbuck task
            okBuck.doLast {
                // Fetch Groovy support deps if needed
                boolean hasGroovyLib = okbuckExt.buckProjects.find {
                    ProjectUtil.getType(it) == ProjectType.GROOVY_LIB
                } != null
                if (hasGroovyLib) {
                    GroovyUtil.setupGroovyHome(project)
                }

                generate(project,
                        okbuckExt,
                        hasGroovyLib ? GroovyUtil.GROOVY_HOME_LOCATION : null)
            }
            // Configure buck projects
            configureBuckProjects(okbuckExt.buckProjects, setupOkbuck)
        }
    }

    private generateIntAndExtLibs(Project project) {
        def res = generateLibs(project, depCache.resolvedDependencies)
        def externals = res[0]
        def internals = res[1]
        writeLibs(project, externals, EXT_LIBS)
        writeLibs(project, internals, INT_LIBS)
    }

    private void writeLibs(Project project, List<String> content, String path) {
        def dir = new File(project.projectDir, path)
        dir.mkdirs()
        def file = new File(dir, BUILD)
        file.text = (content+ [""]).join("\n")
    }

    private static Set<ExternalDependency> generateLibs(Project project,
                                                        Set<ResolvedDependency> deps) {
        Set<ExternalDependency> done = [] as Set
        Stack todo = [] as Stack
        deps.each { todo.push(it) }
        def internal = []
        def external = []
        def cast = { it -> ExternalDependency.fromResolvedDependency(it, INTERNAL_PROJECTS_PREFIX) }
        while (!todo.isEmpty()) {
            def nextElement = todo.pop()
            def nextExtDep = cast(nextElement)
            if (!done.contains(nextExtDep)) {
                def remainingChildren = nextElement.children.findAll { !done.contains(cast(it)) }
                if (remainingChildren.isEmpty()) {
                    def res = ['java_library(',
                               "  name = \"${nextExtDep.toBazelName()}\",",
                               '  visibility = ["//visibility:public"],',
                               '  exports = [']
                    def jarExport = toBazelJavaLibraryExport(nextExtDep)
                    res += [ "    \"${jarExport}\"," ]
                    res += nextElement.children.collect { "    \"${cast(it).toBazelPath()}\"," }
                    res += [   '  ],',
                               ')',
                               '']
                    def list = nextExtDep.isInternal() ? internal : external
                    list.addAll(res)
                    done.add(nextExtDep)
                }
                else {
                    todo.push(nextElement)
                    remainingChildren.each { todo.push(it) }
                }
            }
        }
        new Tuple(external, internal)
    }

    private static String toBazelMavenJarName(ExternalDependency dep) {
        [dep.group,
         dep.name,
         dep.version].collect { "${it}" }.join('_').replace(".", "_").replace("-", "_")
    }

    private static String toBazelJavaLibraryExport(ExternalDependency dep) {
        "@${toBazelMavenJarName(dep)}//${JAR}"
    }

    private String toBazelMavenJar(ExternalDependency dep) {
        def name = toBazelMavenJarName(dep)
        def attributes = [dep.group,
                          dep.name,
                          JAR]
        def classifier = depCache.getClassifier(dep)
        if (classifier) {
            attributes.add(classifier)
        }
        attributes.add(dep.version)
        def artifact = attributes.collect { "${it}" }.join(':')
        "maven_jar(name = \"${name}\", artifact = \"${artifact}\")"
    }

    private void generateWorkspace(Project project) {
        List<String> res = ['maven_server(',
                            '    name = "default",',
                            "    url = \"${INTERNAL_NEXUS}\",",
                            ')']
        res += depCache.getAllDependencies().collect { toBazelMavenJar(it) }
        new File(project.projectDir, this.WORKSPACE).text = (res + [""]).join("\n")
    }

    private void generateBzlRules(Project project) {
        JunitTestsUtil.generate(project)
        AddstattoolUtil.generate(project)
    }

    private static void generate(Project project, OkBuckExtension okbuckExt, String groovyHome) {
        // generate empty .buckconfig if it does not exist
        File dotBuckConfig = project.file(".buckconfig")
        if (!dotBuckConfig.exists()) {
            dotBuckConfig.createNewFile()
        }

        // Setup defs
        FileUtil.copyResourceToProject("defs/OKBUCK_DEFS", project.file(OKBUCK_DEFS))
        Set<String> defs = okbuckExt.extraDefs.collect {
            "//${FileUtil.getRelativePath(project.rootDir, it)}"
        }
        defs.add("//${OKBUCK_DEFS}")

    }

    private static Set<Configuration> configurations(Set<Project> projects) {
        Set<Configuration> configurations = new HashSet() as Set<Configuration>
        projects.each { Project p ->
            ProjectUtil.getTargets(p).values().each {
                if (it instanceof JavaTarget) {
                    configurations.addAll(it.depConfigurations())
                }
            }
        }
        return configurations
    }

    private static void configureBuckProjects(Set<Project> buckProjects, Task setupOkbuck) {
        buckProjects.each { Project buckProject ->
            Task okbuckProjectTask = buckProject.tasks.maybeCreate(OKBUCK)
            okbuckProjectTask.doLast {
                BuckFileGenerator.generate(buckProject)
            }
            okbuckProjectTask.dependsOn(setupOkbuck)
        }
    }

    /**
     * This is required to let the root project super configuration resolve
     * all recursively copied configurations.
     */
    private static void addSubProjectRepos(Project rootProject, Set<Project> subProjects) {
        Map<Object, ArtifactRepository> reduced = [:]

        subProjects.each { Project subProject ->
            subProject.repositories.asMap.values().each {
                if (it instanceof MavenArtifactRepository) {
                    reduced.put(it.url, it)
                } else if (it instanceof IvyArtifactRepository) {
                    reduced.put(it.url, it)
                } else if (it instanceof FlatDirectoryArtifactRepository) {
                    reduced.put(it.dirs, it)
                } else {
                    rootProject.repositories.add(it)
                }
            }
        }

        rootProject.repositories.addAll(reduced.values())
    }
}
