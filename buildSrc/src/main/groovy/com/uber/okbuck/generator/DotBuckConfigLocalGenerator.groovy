package com.uber.okbuck.generator

import com.uber.okbuck.config.DotBuckConfigLocalFile
import com.uber.okbuck.core.model.base.ProjectType
import com.uber.okbuck.core.model.base.Target
import com.uber.okbuck.core.util.ProjectUtil
import com.uber.okbuck.extension.OkBuckExtension
import org.gradle.api.Project

final class DotBuckConfigLocalGenerator {

    private DotBuckConfigLocalGenerator() {}

    /**
     * generate {@link DotBuckConfigLocalFile}
     */
    static DotBuckConfigLocalFile generate(OkBuckExtension okbuck,
                                           String groovyHome,
                                           String proguardJar,
                                           Set<String> defs) {
        Map<String, String> aliases = [:]
        return new DotBuckConfigLocalFile(aliases,
                okbuck.buildToolVersion,
                okbuck.target,
                [".git", "**/.svn"],
                groovyHome,
                proguardJar,
                defs)
    }
}
