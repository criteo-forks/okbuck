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

public final class AddstattoolUtil {

    private static final String ADDSTATTOOL_BZL_FILE = "addstattool/addstattool.bzl";
    private static final String BZL_DIR = "bzl";

    private AddstattoolUtil() {}

    public static String getRule() {
        return ":addstattool";
    }

    public static void generate(Project project) throws IOException {
        FileUtil.copyResourceToProject(ADDSTATTOOL_BZL_FILE,
                                       new File("bzl", "addstattool.bzl"));
        File build = new File(BZL_DIR, "BUILD");
        if (!build.exists()) {
            build.createNewFile();
        }
    }

}
