package com.criteo.tobazel.rule.java

import com.uber.okbuck.rule.base.Rule

final class AntlrRule extends Rule {

    AntlrRule() {
        super("antlrRule")
    }

    @Override
    void print(PrintStream printer) {
        printer.println('load("//bzl:genrule2.bzl", "genrule2")')
        printer.println('genrule2(');
        printer.println('    name = "query_antlr",');
        // put criteo specifig args as arguments
        printer.println('    srcs = ["src/main/antlr4/com/criteo/enginelogs/rowfilter/antlr_parser/RowFilter.g4"],');
        printer.println('    outs = ["query_antlr.srcjar"],');
        printer.println('    cmd = " && ".join([');
        printer.println('        "$(location //antlr:antlr_tool) -package \'com.criteo.enginelogs.rowfilter.antlr_parser\' -o $$TMP $<",');
        printer.println('        "cd $$TMP",');
        printer.println('        "$$ROOT/$(location @bazel_tools//tools/zip:zipper) cC $$ROOT/$@ $$(find *)",');
        printer.println('    ]),');
        printer.println('    tools = [');
        printer.println('        "//antlr:antlr_tool",');
        printer.println('        "@bazel_tools//tools/zip:zipper",');
        printer.println('    ],');
        printer.println(')');
        printer.println('');
        printer.println('java_library(');
        printer.println('    name = "query_parser",');
        printer.println('    srcs = [":query_antlr"],');
        printer.println('    visibility = ["//visibility:public"],');
        printer.println('    deps = [');
        printer.println('        "//antlr:org_antlr_antlr4_runtime_4_5",');
        printer.println('    ],');
        printer.println(')');
        printer.println('');
    }
}
