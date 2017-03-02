package com.criteo.tobazel.core.util;

import java.util.Arrays;
import java.util.List;

public final class ScalaUtil {

    private ScalaUtil() {}

    public static List<String> getWorkspaceLines() {
        return Arrays.asList("git_repository(",
                             "    name = \"io_bazel_rules_scala\",",
                             "    remote = \"https://github.com/bazelbuild/rules_scala.git\",",
                             "    commit = \"73743b830ae98d13a946b25ad60cad5fee58e6d3\", # update this as needed)",
                             ")",
                             "load(\"@io_bazel_rules_scala//scala:scala.bzl\", \"scala_repositories\")",
                             "scala_repositories()",
                             "");
            }

}
