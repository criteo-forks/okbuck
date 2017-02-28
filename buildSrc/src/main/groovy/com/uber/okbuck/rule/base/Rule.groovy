package com.uber.okbuck.rule.base

abstract class Rule {

    final String name

    Rule(String name) {
        this.name = name
    }

    /**
     * Print this rule into the printer.
     */
    abstract void print(PrintStream printer)
}
