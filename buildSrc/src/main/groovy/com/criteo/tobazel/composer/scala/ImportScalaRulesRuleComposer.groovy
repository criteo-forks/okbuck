package com.criteo.tobazel.composer.scala

import com.criteo.tobazel.rule.scala.ImportScalaRulesRule
import com.uber.okbuck.core.model.scala.ScalaLibTarget

// What a name :S
final class ImportScalaRulesRuleComposer {

    private ImportScalaRulesRuleComposer() {
        // no instance
    }

    static ImportScalaRulesRule compose(ScalaLibTarget target) {
        new ImportScalaRulesRule()
    }
}
