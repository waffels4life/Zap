package com.zap.cli;

import com.zap.cli.help.HelpCatalog;
import com.zap.cli.help.HelpFormatter;

import java.io.PrintStream;
import java.util.Objects;

public final class HelpCommandHandler {

    private final HelpCatalog catalog;
    private final HelpFormatter formatter;
    private final PrintStream out;

    public HelpCommandHandler(HelpCatalog catalog,
                              HelpFormatter formatter,
                              PrintStream out) {
        this.catalog = Objects.requireNonNull(catalog, "catalog");
        this.formatter = Objects.requireNonNull(formatter, "formatter");
        this.out = Objects.requireNonNull(out, "out");
    }

     public void handle(Command.Help help) {
        out.println(formatter.overview(catalog.all()));
    }

    public void handle(Command.HelpTopic topic) {
        out.println(formatter.detail(catalog.require(topic.command())));
    }
}