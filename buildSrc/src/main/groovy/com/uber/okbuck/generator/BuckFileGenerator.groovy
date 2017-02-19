package com.uber.okbuck.generator

import com.uber.okbuck.OkBuckGradlePlugin
import com.uber.okbuck.composer.groovy.GroovyLibraryRuleComposer
import com.uber.okbuck.composer.groovy.GroovyTestRuleComposer
import com.uber.okbuck.composer.java.JavaBinaryRuleComposer
import com.uber.okbuck.composer.java.JavaLibraryRuleComposer
import com.uber.okbuck.composer.java.JavaTestRuleComposer
import com.uber.okbuck.config.BUCKFile
import com.uber.okbuck.core.model.base.ProjectType
import com.uber.okbuck.core.model.base.Target
import com.uber.okbuck.core.model.groovy.GroovyLibTarget
import com.uber.okbuck.core.model.java.JavaAppTarget
import com.uber.okbuck.core.model.java.JavaLibTarget
import com.uber.okbuck.core.util.ProjectUtil
import com.uber.okbuck.extension.OkBuckExtension
import com.uber.okbuck.extension.TestExtension
import com.uber.okbuck.rule.base.BuckRule
import com.uber.okbuck.rule.base.GenRule
import org.apache.commons.io.IOUtils
import org.gradle.api.Project

final class BuckFileGenerator {

    private BuckFileGenerator() {}

    /**
     * generate {@code BUCKFile}
     */
    static Map<Project, BUCKFile> generate(Project project) {
        OkBuckExtension okbuck = project.rootProject.okbuck

        TestExtension test = okbuck.test
        List<BuckRule> rules = createRules(project, test.espresso)

        if (rules) {
            BUCKFile buckFile = new BUCKFile(rules)
            PrintStream buckPrinter = new PrintStream(project.file(OkBuckGradlePlugin.BUCK))
            buckFile.print(buckPrinter)
            IOUtils.closeQuietly(buckPrinter)
        }
    }

    private static List<BuckRule> createRules(Project project, boolean espresso) {
        List<BuckRule> rules = []
        ProjectType projectType = ProjectUtil.getType(project)
        ProjectUtil.getTargets(project).each { String name, Target target ->
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

    private static List<BuckRule> createRules(JavaLibTarget target) {
        List<BuckRule> rules = []
        rules.add(JavaLibraryRuleComposer.compose(target))

        if (target.test.sources) {
            rules.add(JavaTestRuleComposer.compose(target))
        }
        return rules
    }

    private static List<BuckRule> createRules(JavaAppTarget target) {
        List<BuckRule> rules = []
        rules.addAll(createRules((JavaLibTarget) target))
        rules.add(JavaBinaryRuleComposer.compose(target))
        return rules
    }

    private static List<BuckRule> createRules(GroovyLibTarget target) {
        List<BuckRule> rules = []
        rules.add(GroovyLibraryRuleComposer.compose(target))

        if (target.test.sources) {
            rules.add(GroovyTestRuleComposer.compose(target))
        }
        return rules
    }
}
