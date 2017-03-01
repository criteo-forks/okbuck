package com.criteo.tobazel.composer.misc

import com.criteo.tobazel.rule.misc.ImportAddstattoolRule
import com.criteo.tobazel.core.model.java.JavaLibTarget

final class ImportAddstattoolRuleComposer {

    private ImportAddstattoolRuleComposer() {
        // no instance
    }

    static ImportAddstattoolRule compose(JavaLibTarget target) {
        new ImportAddstattoolRule()
    }
}
