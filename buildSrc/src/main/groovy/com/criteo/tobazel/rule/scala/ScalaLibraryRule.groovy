package com.criteo.tobazel.rule.scala

import com.uber.okbuck.core.model.base.RuleType
import com.criteo.tobazel.rule.java.JavaRule

final class ScalaLibraryRule extends JavaRule {

    ScalaLibraryRule(
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
            List<String> testTargets,
            Set<String> extraOpts = []) {

        super(RuleType.SCALA_2_10_LIBRARY,
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
              null,
              testTargets,
              null,
              extraOpts)
    }

    private String srcSuffix(String source) {
        source.contains("scala") ? "scala" : "java"
    }

    protected void printSources(PrintStream printer) {
        printer.println("\tsrcs = glob([")
        for (String src : mSrcSet) {
            printer.println("\t\t'${src}/**/*.${srcSuffix(src)}',")
        }
        printer.println("\t]),")
    }
}
