package com.criteo.tobazel.config;

import com.uber.okbuck.rule.base.Rule;
import com.uber.okbuck.config.BuckConfigFile;
import java.io.PrintStream;
import java.util.List;

public final class BazelFile extends BuckConfigFile {

    private final List<Rule> rules;

    public BazelFile(List<Rule> rules) {
        this.rules = rules;
    }

    @Override
    public final void print(PrintStream printer) {
        for (Rule rule : rules) {
            rule.print(printer);
        }
    }
}
