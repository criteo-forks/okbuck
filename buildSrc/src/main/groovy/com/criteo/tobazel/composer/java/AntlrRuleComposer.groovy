package com.criteo.tobazel.composer.java

import com.criteo.tobazel.rule.java.AntlrRule
import com.criteo.tobazel.core.model.java.JavaLibTarget

final class AntlrRuleComposer {

    private AntlrRuleComposer() {
        // no instance
    }

    static AntlrRule compose(JavaLibTarget target) {
        new AntlrRule()
    }
}
