package com.criteo.tobazel.composer.java

import com.criteo.tobazel.composer.jvm.JvmRuleComposer
import com.criteo.tobazel.core.util.AddstattoolUtil
import com.criteo.tobazel.rule.java.JavaLibraryRule

import com.uber.okbuck.core.model.base.RuleType
import com.uber.okbuck.core.model.java.JavaLibTarget
import com.uber.okbuck.core.util.RetrolambdaUtil
import com.uber.okbuck.core.dependency.ExternalDependency

final class JavaLibraryRuleComposer extends JvmRuleComposer {

    private JavaLibraryRuleComposer() {
        // no instance
    }
    static JavaLibraryRule compose(JavaLibTarget target) {
        List<String> deps = []
        deps.addAll(target.main.firstLevelDeps.collect { it.toBazelPath() } as Set)
        deps.addAll(targets(target.main.targetDeps))

        Set<String> aptDeps = [] as Set
        aptDeps.addAll(externalApt(target.apt.externalDeps))
        aptDeps.addAll(targetsApt(target.apt.targetDeps))

        Set<String> providedDeps = []
        providedDeps.addAll(external(target.provided.externalDeps))
        providedDeps.addAll(targets(target.provided.targetDeps))
        providedDeps.removeAll(deps)

        if (target.retrolambda) {
            providedDeps.add(RetrolambdaUtil.getRtStubJarRule())
        }

        if (target.addstattool) {
            deps.add(AddstattoolUtil.getRule())
        }

        List<String> testTargets = []
        if (target.test.sources) {
            testTargets.add(":${test(target)}")
        }

        new JavaLibraryRule(
                src(target),
                ["//visibility:public"],
                deps,
                target.main.sources,
                target.annotationProcessors,
                aptDeps,
                [] as Set,
                target.main.resourcesDir,
                null,
                null,
                target.postprocessClassesCommands,
                target.main.jvmArgs,
                [], //testTargets,
                target.getExtraOpts(RuleType.JAVA_LIBRARY))
    }
}
