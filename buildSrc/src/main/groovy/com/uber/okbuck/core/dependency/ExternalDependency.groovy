package com.uber.okbuck.core.dependency

import org.apache.commons.io.FilenameUtils
import org.apache.maven.artifact.versioning.DefaultArtifactVersion
import org.gradle.api.artifacts.ModuleIdentifier
import org.gradle.api.artifacts.ModuleVersionIdentifier
import org.gradle.api.internal.artifacts.DefaultModuleVersionIdentifier
import org.gradle.api.artifacts.ResolvedDependency

class ExternalDependency extends VersionlessDependency {

    static final String LOCAL_DEP_VERSION = "1.0.0"
    static final String SOURCES_JAR = '-sources.jar'

    final String internalProjectsPrefix

    final DefaultArtifactVersion version
    final File depFile

    ExternalDependency(ModuleVersionIdentifier identifier, File depFile, String classifier,
                       String internalProjectsPrefix) {
        super(identifier, classifier)
        if (identifier.version) {
            version = new DefaultArtifactVersion(identifier.version)
        } else {
            version = new DefaultArtifactVersion(LOCAL_DEP_VERSION)
        }

        this.depFile = depFile
        this.internalProjectsPrefix = internalProjectsPrefix
    }

    @Override
    String toString() {
        if (classifier) {
            return "${this.group}:${this.name}:${this.version}-${this.classifier} -> ${this.depFile.toString()}"
        }
        return "${this.group}:${this.name}:${this.version} -> ${this.depFile.toString()}"
    }

    String getCacheName(boolean useFullDepName = false) {
        if (useFullDepName) {
            if (group) {
                return "${group}.${depFile.name}" as String
            } else {
                return "${name}.${depFile.name}" as String
            }
        } else {
            return depFile.name
        }
    }

    String getSourceCacheName(boolean useFullDepName = false) {
        return getCacheName(useFullDepName).replaceFirst(/\.(jar|aar)$/, SOURCES_JAR)
    }

    Boolean isInternal() {
        internalProjectsPrefix ? group.startsWith(internalProjectsPrefix) : false
    }


    static ExternalDependency fromLocal(File localDep, String internalProjectsPrefix) {
        String baseName = FilenameUtils.getBaseName(localDep.name)
        ModuleVersionIdentifier identifier = getDepIdentifier(
                baseName,
                baseName,
                LOCAL_DEP_VERSION)
        return new ExternalDependency(identifier, localDep, null, internalProjectsPrefix)
    }

    static ExternalDependency fromResolvedDependency(ResolvedDependency dependency, String internalProjectsPrefix) {
        ModuleVersionIdentifier identifier = new DefaultModuleVersionIdentifier(dependency.moduleGroup,
                                                                                dependency.moduleName,
                                                                                dependency.moduleVersion)
        return new ExternalDependency(identifier, null, null, internalProjectsPrefix)
    }

    String toBazelName() {
        def start = [group, name]
        def res = classifier ? start + classifier : start
        res.join("#")
    }

    String toBazelPath() {
        def preamble = isInternal() ? "intlibs" : "extlibs"
        "//${preamble}:${toBazelName()}"
    }
}
