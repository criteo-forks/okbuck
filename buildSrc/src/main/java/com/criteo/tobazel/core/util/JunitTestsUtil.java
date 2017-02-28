package com.criteo.tobazel.core.util;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.uber.okbuck.OkBuckGradlePlugin;
import com.uber.okbuck.core.dependency.DependencyCache;
import com.uber.okbuck.core.model.base.Scope;
import com.uber.okbuck.extension.RetrolambdaExtension;

import org.apache.commons.lang.StringUtils;
import org.gradle.api.Project;

import java.io.File;
import java.util.Collections;
import java.io.IOException;

public final class JunitTestsUtil {

    private static final String JUNIT_TESTS_BZL_FILE = "junit_tests/junit_tests.bzl";
    private static final String BZL_DIR = "bzl";

    private JunitTestsUtil() {}

    public static void generate(Project project) throws IOException {
        FileUtil.copyResourceToProject(JUNIT_TESTS_BZL_FILE,
                                       new File("bzl", "junit_tests.bzl"));
        File build = new File(BZL_DIR, "BUILD");
        if (!build.exists()) {
            build.createNewFile();
        }
    }

}
