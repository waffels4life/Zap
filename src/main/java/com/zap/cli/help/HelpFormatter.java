package com.zap.cli.help;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class HelpFormatter {

    private static final String INDENT = "  ";
    private static final String GAP = "   ";

    public String overview(List<CommandDoc> docs) {
        Objects.requireNonNull(docs, "docs");

        int width = docs.stream()
                .mapToInt(doc -> doc.label().length())
                .max()
                .orElse(0);

        List<String> lines = new ArrayList<>();
        lines.add("Zap - Fast Java project runner");
        lines.add("");
        lines.add("Usage:");
        lines.add(INDENT + "zap <command> [options]");
        lines.add("");
        lines.add("Commands:");
        for (CommandDoc doc : docs) {
            lines.add(INDENT + pad(doc.label(), width) + GAP + doc.summary());
        }
        lines.add("");
        lines.add("Run `zap help <command>` for details about one command.");

        return join(lines);
    }

    public String detail(CommandDoc doc) {
        Objects.requireNonNull(doc, "doc");

        List<String> lines = new ArrayList<>();
        lines.add("zap " + doc.name() + " - " + doc.summary());

        addSection(lines, "Usage:", doc.usages());
        addSection(lines, "Description:", doc.description());
        addOptions(lines, doc.optionDocs());
        addSection(lines, "Examples:", doc.examples());

        if (!doc.aliases().isEmpty()) {
            lines.add("");
            lines.add("Aliases: " + String.join(", ", doc.aliases()));
        }

        return join(lines);
    }

    private static void addSection(List<String> lines, String title, List<String> body) {
        if (body.isEmpty()) return;

        lines.add("");
        lines.add(title);
        body.forEach(line -> lines.add(INDENT + line));
    }

    private static void addOptions(List<String> lines, List<OptionDoc> options) {
        if (options.isEmpty()) return;

        int width = options.stream()
                .mapToInt(option -> option.syntax().length())
                .max()
                .orElse(0);

        lines.add("");
        lines.add("Options:");
        for (OptionDoc option : options)
            lines.add(INDENT
                    + pad(option.syntax(), width)
                    + GAP
                    + option.description()
            );
    }

    private static String pad(String text, int width) {
        return text + " ".repeat(Math.max(0, width - text.length()));
    }

    private static String join(List<String> lines) {
        return String.join(System.lineSeparator(), lines);
    }
}