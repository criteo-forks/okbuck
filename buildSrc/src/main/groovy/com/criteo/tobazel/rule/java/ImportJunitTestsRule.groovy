package com.criteo.tobazel.rule.java

import com.uber.okbuck.rule.base.Rule

final class ImportJunitTestsRule extends Rule {

    ImportJunitTestsRule() {
        super("importJunitTests")
    }

    @Override
    void print(PrintStream printer) {
        printer.println('load("//bzl:junit_tests.bzl", "junit_tests")')
    }
}
