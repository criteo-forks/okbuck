package com.criteo.tobazel.core.util;

import java.util.Arrays;
import java.util.List;

public final class ScalaUtil {

    private ScalaUtil() {}

    public static List<String> getWorkspaceLines() {
        return Arrays.asList("git_repository(",
                             "    name = \"io_bazel_rules_scala\",",
                             "    remote = \"https://github.com/criteo-forks/rules_scala.git\",",
                             "    commit = \"6c6ea9ae5a9a2dd4c253da9fabdab8a3fdaf988e\",",
                             ")",
                             "load(\"@io_bazel_rules_scala//scala:scala.bzl\", \"scala_repositories\", \"scala210_repositories\")",
                             "scala_repositories()",
                             "scala210_repositories()",
                             "");
            }

}
