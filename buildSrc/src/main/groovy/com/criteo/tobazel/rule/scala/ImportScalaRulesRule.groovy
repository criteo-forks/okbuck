package com.criteo.tobazel.rule.scala

import com.uber.okbuck.rule.base.Rule

final class ImportScalaRulesRule extends Rule {

    ImportScalaRulesRule() {
        super("importScalaRules")
    }

    @Override
    void print(PrintStream printer) {
        printer.println("load(\"@io_bazel_rules_scala//scala:scala.bzl\", \"scala_2_10_library\", \"scala_test\")")
        printer.println("")
    }
}
