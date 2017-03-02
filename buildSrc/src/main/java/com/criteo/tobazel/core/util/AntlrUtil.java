package com.criteo.tobazel.core.util;

import org.gradle.api.Project;

import java.io.File;
import java.io.IOException;

import java.util.Collections;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

public final class AntlrUtil {

    private static final String GENRULE2_BZL_FILE = "antlr/genrule2.bzl";
    private static final String BZL_DIR = "bzl";
    private static final String ANTLR_BUILD_FILE = "antlr/BUILD";
    private static final String ANTLR_DIR = "antlr";

    private AntlrUtil() {}

    public static List<String> modifyDeps(List<String> deps) {
        List<String> res = deps.stream()
            .filter(x -> !x.equals("//extlibs:org.antlr-antlr4"))
            .collect(Collectors.toCollection(ArrayList::new));
        res.addAll(Arrays.asList("//antlr:org_antlr_antlr4_runtime_4_5",
                                 "//antlr:org_antlr_antlr_runtime_3_5_2",
                                 ":query_parser"));
        return res;
    }

    // TODO: add these in dependencies in DepCache
    public static List<String> getWorkspaceLines() {
        return Arrays.asList("maven_jar(name = \"org_antlr_antlr4_runtime_4_5\", artifact = \"org.antlr:antlr4-runtime:jar:4.5\")",
                             "maven_jar(name = \"org_antlr_antlr4\", artifact = \"org.antlr:antlr4:jar:4.5\")");
    }

    public static void generate(Project project) throws IOException {
        FileUtil.copyResourceToProject(GENRULE2_BZL_FILE,
                                       new File("bzl", "genrule2.bzl"));
        File build = new File(BZL_DIR, "BUILD");
        if (!build.exists()) {
            build.createNewFile();
        }
        FileUtil.copyResourceToProject(ANTLR_BUILD_FILE,
                                       new File(ANTLR_DIR, "BUILD"));

    }

}
