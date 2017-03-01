package com.criteo.tobazel.rule.java

import com.uber.okbuck.core.model.base.RuleType

abstract class JavaRule extends com.uber.okbuck.rule.java.JavaRule {
    JavaRule(
            RuleType ruleType,
            String name,
            List<String> visibility,
            List<String> deps,
            Set<String> srcSet,
            Set<String> annotationProcessors,
            Set<String> aptDeps,
            Set<String> providedDeps,
            String resourcesDir,
            String sourceCompatibility,
            String targetCompatibility,
            List<String> postprocessClassesCommands,
            List<String> options,
            List<String> testRunnerJvmArgs,
            List<String> testTargets,
            List<String> labels = null,
            Set<String> extraOpts = []) {

        super(ruleType,
              name,
              visibility,
              deps,
              srcSet,
              annotationProcessors,
              aptDeps,
              providedDeps,
              resourcesDir,
              sourceCompatibility,
              targetCompatibility,
              postprocessClassesCommands,
              options,
              testRunnerJvmArgs,
              testTargets,
              labels,
              extraOpts)
    }

    protected void printResourcesDir(PrintStream printer) {
        printer.println("\tresources = glob([")
        printer.println("\t\t'${mResourcesDir}/**',")
        printer.println("\t]),")
    }
}
