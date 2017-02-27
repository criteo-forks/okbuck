package com.criteo.tobazel.composer.jvm;

import com.uber.okbuck.core.dependency.ExternalDependency;
import com.uber.okbuck.composer.jvm.JvmBuckRuleComposer;

public class JvmRuleComposer extends JvmBuckRuleComposer {

    public static String toBazelDep(ExternalDependency dep) {
        String depdir = dep.isInternal() ? "intlibs" : "extlibs";
        return depdir + "/" + dep.getName();
    }
}
