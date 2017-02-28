package com.criteo.tobazel.composer.java

import com.criteo.tobazel.rule.java.ImportJunitTestsRule
import com.uber.okbuck.core.model.java.JavaLibTarget

final class ImportJunitTestsRuleComposer {

    private ImportJunitTestsRuleComposer() {
        // no instance
    }

    static ImportJunitTestsRule compose(JavaLibTarget target) {
        new ImportJunitTestsRule()
    }
}
