package com.criteo.tobazel.generator

import com.criteo.tobazel.core.model.java.JavaLibTarget
import com.criteo.tobazel.composer.java.JavaLibraryRuleComposer
import com.criteo.tobazel.composer.java.JavaTestRuleComposer
import com.criteo.tobazel.composer.java.ImportJunitTestsRuleComposer
import com.criteo.tobazel.composer.misc.ImportAddstattoolRuleComposer
import com.criteo.tobazel.config.BazelFile

import com.uber.okbuck.OkBuckGradlePlugin
import com.uber.okbuck.composer.groovy.GroovyLibraryRuleComposer
import com.uber.okbuck.composer.groovy.GroovyTestRuleComposer
import com.uber.okbuck.composer.java.JavaBinaryRuleComposer

import com.uber.okbuck.core.model.base.ProjectType
import com.uber.okbuck.core.model.base.Target
import com.uber.okbuck.core.model.groovy.GroovyLibTarget
import com.uber.okbuck.core.model.java.JavaAppTarget
import com.uber.okbuck.core.util.ProjectUtil
import com.uber.okbuck.extension.OkBuckExtension
import com.uber.okbuck.extension.TestExtension
import com.uber.okbuck.rule.base.Rule
import com.uber.okbuck.rule.base.GenRule
import org.apache.commons.io.IOUtils
import org.gradle.api.Project

final class BazelFileGenerator {

    private BazelFileGenerator() {}

    /**
     * generate {@code BazelFile}
     */
    static Map<Project, BazelFile> generate(Project project) {
        OkBuckExtension okbuck = project.rootProject.okbuck

        TestExtension test = okbuck.test
        List<Rule> rules = createRules(project, test.espresso)

        if (rules) {
            BazelFile bazelFile = new BazelFile(rules)
            PrintStream bazelPrinter = new PrintStream(project.file(OkBuckGradlePlugin.BUILD))
            bazelFile.print(bazelPrinter)
            IOUtils.closeQuietly(bazelPrinter)
        }
    }

    private static List<Rule> createRules(Project project, boolean espresso) {
        List<Rule> rules = []
        ProjectType projectType = ProjectUtil.getType(project)
        ProjectUtil.getTargets(project, ProjectUtil.BuildSystem.BAZEL).each { String name, Target target ->
            switch (projectType) {
                case ProjectType.JAVA_LIB:
                    rules.addAll(createRules((JavaLibTarget) target))
                    break
                case ProjectType.JAVA_APP:
                    rules.addAll(createRules((JavaAppTarget) target))
                    break
                case ProjectType.GROOVY_LIB:
                    rules.addAll(createRules((GroovyLibTarget) target))
                    break
                default:
                    break
            }
        }

        // de-dup rules by name
        rules = rules.unique { rule ->
            rule.name
        }

        return rules
    }

    private static List<Rule> createRules(JavaLibTarget target) {
        List<Rule> rules = []
        rules.add(JavaLibraryRuleComposer.compose(target))

        if (target.test.sources) {
            rules.add(ImportJunitTestsRuleComposer.compose(target))
            rules.add(JavaTestRuleComposer.compose(target))
        }
        if (target.addstattool) {
            rules.add(ImportAddstattoolRuleComposer.compose(target))
        }
        return rules
    }

    private static List<Rule> createRules(JavaAppTarget target) {
        List<Rule> rules = []
        rules.addAll(createRules((JavaLibTarget) target))
        rules.add(JavaBinaryRuleComposer.compose(target))
        return rules
    }

    private static List<Rule> createRules(GroovyLibTarget target) {
        List<Rule> rules = []
        rules.add(GroovyLibraryRuleComposer.compose(target))

        if (target.test.sources) {
            rules.add(GroovyTestRuleComposer.compose(target))
        }
        return rules
    }
}
