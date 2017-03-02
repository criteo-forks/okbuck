package com.uber.okbuck.core.model.scala

import com.uber.okbuck.core.model.base.Scope
import com.criteo.tobazel.core.model.java.JavaLibTarget
import org.gradle.api.Project

/**
 * A scala library target
 */
class ScalaLibTarget extends JavaLibTarget {

    ScalaLibTarget(Project project, String name) {
        super(project, name)
    }

    @Override
    Scope getMain() {
        return new Scope(project,
                compileConfigs,
                project.files("src/main/java") + project.files("src/main/scala") as Set,
                project.file("src/main/resources"),
                project.compileJava.options.compilerArgs as List)
    }

    @Override
    Scope getTest() {
        return new Scope(project,
                expand(compileConfigs, TEST_PREFIX, true),
                project.files("src/test/java") + project.files("src/test/scala") as Set,
                project.file("src/test/resources"),
                project.compileTestJava.options.compilerArgs as List)
    }
}
