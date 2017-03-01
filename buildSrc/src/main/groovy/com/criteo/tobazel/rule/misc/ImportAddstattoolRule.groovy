package com.criteo.tobazel.rule.misc

import com.uber.okbuck.rule.base.Rule

final class ImportAddstattoolRule extends Rule {

    ImportAddstattoolRule() {
        super("importAddstattool")
    }

    @Override
    void print(PrintStream printer) {
        printer.println('load("//bzl:addstattool.bzl", "addstattool")')
        printer.println('addstattool()')
    }
}
