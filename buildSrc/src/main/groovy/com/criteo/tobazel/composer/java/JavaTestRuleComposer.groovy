package com.criteo.tobazel.composer.java

import com.criteo.tobazel.composer.jvm.JvmRuleComposer
import com.criteo.tobazel.rule.java.JavaTestRule
import com.uber.okbuck.core.model.base.RuleType
import com.uber.okbuck.core.model.java.JavaLibTarget
import com.uber.okbuck.core.util.RetrolambdaUtil

final class JavaTestRuleComposer extends JvmRuleComposer {

    private JavaTestRuleComposer() {
        // no instance
    }

    static JavaTestRule compose(JavaLibTarget target) {
        List<String> deps = []
        deps.add(":${src(target)}")
        deps.addAll(target.test.firstLevelDeps.collect { it.toBazelPath() } as Set)
        deps.addAll(targets(target.test.targetDeps))

        Set<String> aptDeps = [] as Set
        aptDeps.addAll(external(target.testApt.externalDeps))
        aptDeps.addAll(targets(target.testApt.targetDeps))

        Set<String> providedDeps = []
        providedDeps.addAll(external(target.testProvided.externalDeps))
        providedDeps.addAll(targets(target.testProvided.targetDeps))
        providedDeps.removeAll(deps)

        if (target.retrolambda) {
            providedDeps.add(RetrolambdaUtil.getRtStubJarRule())
        }

        new JavaTestRule(
                test(target),
                ["//visibility:public"],
                deps,
                target.test.sources,
                target.testAnnotationProcessors,
                aptDeps,
                [] as Set,
                target.test.resourcesDir,
                null,
                null,
                target.postprocessClassesCommands,
                target.test.jvmArgs,
                null,
                target.getExtraOpts(RuleType.JAVA_TEST))
    }
}
