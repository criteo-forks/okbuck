package com.criteo.tobazel.core.model.base;

import com.uber.okbuck.core.model.base.ProjectType;
import com.uber.okbuck.core.model.base.Target;
import com.uber.okbuck.core.model.groovy.GroovyLibTarget;
import com.uber.okbuck.core.model.java.JavaAppTarget;
import com.criteo.tobazel.core.model.java.JavaLibTarget;
import com.uber.okbuck.core.model.jvm.JvmTarget;
import com.uber.okbuck.core.util.ProjectUtil;

import org.gradle.api.Project;

import java.util.Collections;
import java.util.Map;

public class TargetCache extends com.uber.okbuck.core.model.base.TargetCache {

    @Override
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
                default:
                    projectTargets = Collections.emptyMap();
                    break;
            }
            store.put(project, projectTargets);
        }

        return projectTargets;
    }
}
