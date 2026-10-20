package com.zap.cli.help;

import com.zap.exception.cli.CommandSyntaxException;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

public final class HelpCatalog {

    private final List<CommandDoc> docs;

    public HelpCatalog(List<CommandDoc> docs) {
        Objects.requireNonNull(docs, "docs");
        this.docs = List.copyOf(docs);
    }

    public List<CommandDoc> all() {
        return docs;
    }

    /*
     * Finds a command by name or alias. Throws if there is no such command.
     */
    public CommandDoc require(String token) {
        Objects.requireNonNull(token, "token");

        for (CommandDoc doc : docs)
            if (doc.matches(token))
                return doc;

        throw CommandSyntaxException.unknownCommand(token);
    }

    public static HelpCatalog standard() {
        return new HelpCatalog(List.of(
                initDoc(),
                runDoc(),
                configDoc(),
                helpDoc(),
                versionDoc()));
    }

    private static CommandDoc initDoc() {
        return new CommandDoc(
                "init",
                List.of(),
                "Initialize Zap in the current project",
                List.of(
                        "zap init",
                        "zap init auto",
                        "zap init <package.Class>"),
                List.of(
                        "Creates .zap/config.properties in the current folder.",
                        "With a class name, the name format and its source file are",
                        "checked first. If any check fails, nothing is written.",
                        "Fails if the project is already initialized."),
                List.of(
                        new OptionDoc("auto",
                                "Find Main.java automatically (default)"),
                        new OptionDoc("<package.Class>",
                                "Fully qualified name of the entry point")),
                List.of(
                        "zap init",
                        "zap init com.app.Start"));
    }

    private static CommandDoc runDoc() {
        return new CommandDoc(
                "run",
                List.of("-r"),
                "Compile and run the project",
                List.of(
                        "zap run [--input <value>...] [--args <value>...]",
                        "zap -r"),
                List.of(
                        "Compiles every .java file in the source directory, verifies",
                        "the main class, and runs it. Works from the project root or",
                        "from any folder inside it.",
                        "Values given here replace the saved ones for this run only.",
                        "The exit code of your program becomes the exit code of Zap."),
                List.of(
                        new OptionDoc("--input <value>...",
                                "Lines sent to the program's standard input"),
                        new OptionDoc("--args <value>...",
                                "Program arguments (the String[] args)")),
                List.of(
                        "zap run",
                        "zap run --input Ali 20",
                        "zap run --args --verbose data.txt",
                        "zap run --input \"hello world\" --args -v"));
    }

    private static CommandDoc configDoc() {
        return new CommandDoc(
                "config",
                List.of(),
                "Change the saved settings",
                List.of(
                        "zap config --input <value>...",
                        "zap config --args <value>...",
                        "zap config --main <package.Class>",
                        "zap config <--input|--args|--main> --reset",
                        "zap config --show",
                        "zap config --reset-all"),
                List.of(
                        "Exactly one option per command.",
                        "Every change is validated before it is written. A rejected",
                        "change leaves the configuration file untouched."),
                List.of(
                        new OptionDoc("--input <value>...",
                                "Save the inputs used on every run"),
                        new OptionDoc("--args <value>...",
                                "Save the program arguments used on every run"),
                        new OptionDoc("--main <package.Class>",
                                "Set the entry point (name and source file are checked)"),
                        new OptionDoc("--reset",
                                "With --input, --args or --main: restore that setting"),
                        new OptionDoc("--show",
                                "Print the current settings"),
                        new OptionDoc("--reset-all",
                                "Restore every setting to its default")),
                List.of(
                        "zap config --input Sara 30",
                        "zap config --main com.app.Start",
                        "zap config --main --reset",
                        "zap config --show"));
    }

    private static CommandDoc helpDoc() {
        return new CommandDoc(
                "help",
                List.of("-h", "--help"),
                "Show help for Zap or for one command",
                List.of(
                        "zap help",
                        "zap help <command>"),
                List.of(
                        "Without a command name, lists all commands.",
                        "With a command name, shows its usage, options and examples."),
                List.of(),
                List.of(
                        "zap help",
                        "zap help run"));
    }

    private static CommandDoc versionDoc() {
        return new CommandDoc(
                "version",
                List.of("-v", "--version"),
                "Show the version",
                List.of("zap version"),
                List.of("Prints the installed Zap version."),
                List.of(),
                List.of("zap version"));
    }
}