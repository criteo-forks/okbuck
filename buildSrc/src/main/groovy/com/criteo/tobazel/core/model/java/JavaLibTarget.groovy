package com.criteo.tobazel.core.model.java

import org.gradle.api.Project;

class JavaLibTarget extends com.uber.okbuck.core.model.java.JavaLibTarget {
    JavaLibTarget(Project project, String name) {
        super(project, name)
    }

    boolean getAddstattool() {
        // project.plugins.hasPlugin does not work
        project.plugins.any { it.toString().startsWith("AdStatToolPlugin") }
    }
}
