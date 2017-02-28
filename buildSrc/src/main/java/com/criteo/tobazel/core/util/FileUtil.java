package com.criteo.tobazel.core.util;

import org.apache.commons.io.IOUtils;
import org.gradle.api.Project;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public final class FileUtil {

    private FileUtil() {}

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void copyResourceToProject(String resource, File destination) {
        com.uber.okbuck.core.util.FileUtil.copyResourceToProject(FileUtil.class.getResourceAsStream(resource), destination);
    }
}
