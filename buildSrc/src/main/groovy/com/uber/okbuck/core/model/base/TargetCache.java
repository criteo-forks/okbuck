package com.uber.okbuck.core.model.base;

import com.uber.okbuck.core.model.groovy.GroovyLibTarget;
import com.uber.okbuck.core.model.java.JavaAppTarget;
import com.uber.okbuck.core.model.java.JavaLibTarget;
import com.uber.okbuck.core.model.scala.ScalaLibTarget;
import com.uber.okbuck.core.model.jvm.JvmTarget;
import com.uber.okbuck.core.util.ProjectUtil;

import org.gradle.api.Project;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class TargetCache {

    protected final Map<Project, Map<String, Target>> store = new HashMap<>();
    private final Map<File, Target> outputToTarget = new HashMap<>();

    public Map<String, Target> getTargets(Project project) {
        Map<String, Target> projectTargets = store.get(project);
        if (projectTargets == null) {
            ProjectType type = ProjectUtil.getType(project);
            switch (type) {
                case GROOVY_LIB:
                    projectTargets = Collections.singletonMap(JvmTarget.MAIN,
                            (Target) new GroovyLibTarget(project, JvmTarget.MAIN));
                    break;
                case JAVA_APP:
                    projectTargets = Collections.singletonMap(JvmTarget.MAIN,
                            (Target) new JavaAppTarget(project, JvmTarget.MAIN));
                    break;
                case JAVA_LIB:
                    projectTargets = Collections.singletonMap(JvmTarget.MAIN,
                            (Target) new JavaLibTarget(project, JvmTarget.MAIN));
                    break;
                case SCALA_LIB:
                    projectTargets = Collections.singletonMap(JvmTarget.MAIN,
                            (Target) new ScalaLibTarget(project, JvmTarget.MAIN));
                    break;
                default:
                    projectTargets = Collections.emptyMap();
                    break;
            }
            store.put(project, projectTargets);
        }

        return projectTargets;
    }

    @Nullable
    public Target getTargetForOutput(Project targetProject, File output) {
        Target result;
        ProjectType type = ProjectUtil.getType(targetProject);
        switch (type) {
            case ANDROID_LIB:
                result = outputToTarget.get(output);
                break;
            case GROOVY_LIB:
            case JAVA_APP:
            case JAVA_LIB:
            case SCALA_LIB:
                result = getTargets(targetProject).values().iterator().next();
                break;
            default:
                result = null;
        }
        return result;
    }
}
