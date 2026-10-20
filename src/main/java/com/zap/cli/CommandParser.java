package com.zap.cli;

import com.zap.config.ConfigKeys;
import com.zap.exception.cli.CommandSyntaxException;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public final class CommandParser {

    /*
     * Every command line has it own separated list.
     * one for execution and one for setting.
     */
    private static final Set<String> RUN_OPTIONS =
            Set.of("--input", "--args");

    private static final Set<String> CONFIG_OPTIONS =
            Set.of("--input", "--args", "--main", "--show", "--reset-all", "--reset");

    public Command parse(String[] args) {
        // not nullable check
        Objects.requireNonNull(args, "args");

        /*
         * if its empty, I consider it that you still don't know how
         * work with zap.
         *
         * don't hesitate. type something!
         */
        if (args.length == 0)
            return new Command.Help();

        // first argument is the command word
        String name = args[0];

        /*
         * I put other commands in a separate list. it's still the same data, and
         * we are not returning a copy version of it.
         */
        List<String> rest = List.of(args).subList(1, args.length);

        return switch (name) {
            case "init" -> parseInit(rest);
            case "run", "-r" -> parseRun(rest);
            case "config" -> parseConfig(rest);
            case "help", "-h", "--help" -> parseHelp(rest);
            case "version", "-v", "--version" -> parseVersion(rest);
            default -> throw CommandSyntaxException.unknownCommand(name);
        };
    }

    /*
     * parse()
     *   |-- parseInit     (count check)
     *   |-- parseHelp     (count check)
     *   |-- parseVersion  (empty check)
     *   |-- parseRun      -> readOptions -> values
     *   |-- parseConfig   -> readOptions -> requireFlag
     *                                    -> resetOnly
     *                                    -> requireValues
     *                                    -> requireSingle
     */

    private static Command parseInit(List<String> rest) {
        /*
         * No value             >> pick the default option
         * One value            >> the main class package
         * More than one value  >> Error
         */
        return switch (rest.size()) {
            case 0 -> new Command.Init(ConfigKeys.AUTO_MAIN_CLASS);
            case 1 -> new Command.Init(rest.getFirst());
            default -> throw CommandSyntaxException.invalidUsage(
                    "init", "expected at most one class name");
        };
    }

    private static Command parseRun(List<String> rest) {
        Map<String, List<String>> options = readOptions(
                "run", rest, RUN_OPTIONS
        );

        return new Command.Run(
                values("run", "--input", options),
                values("run", "--args", options)
        );
    }

    private static Command parseHelp(List<String> rest) {
        return switch (rest.size()) {
            case 0 -> new Command.Help();
            case 1 -> new Command.HelpTopic(rest.getFirst());
            default -> throw CommandSyntaxException.invalidUsage(
                    "help", "expected at most one command name"
            );
        };
    }

    private static Command parseVersion(List<String> rest) {
        if (!rest.isEmpty())
            throw CommandSyntaxException.unknownOption(
                    "version", rest.getFirst()
            );

        return new Command.Version();
    }

    private static Command parseConfig(List<String> tokens) {
        Map<String, List<String>> options = readOptions(
                "config", tokens, CONFIG_OPTIONS
        );

        List<String> resetValues = options.remove("--reset");
        boolean reset = resetValues != null;

        if (reset && !resetValues.isEmpty())
            throw CommandSyntaxException.invalidUsage(
                    "config", "--reset takes no values"
            );

        if (options.size() != 1)
            throw CommandSyntaxException.invalidUsage(
                    "config", "expected exactly one option"
            );

        String option = options.keySet().iterator().next();
        List<String> values = options.get(option);

        return switch (option) {
            case "--show" -> {
                requireFlag(option, values, reset);
                yield new Command.ConfigShow();
            }
            case "--reset-all" -> {
                requireFlag(option, values, reset);
                yield new Command.ConfigResetAll();
            }
            case "--input" -> reset
                    ? resetOnly(option, values, new Command.ConfigResetInput())
                    : new Command.ConfigSetInput(requireValues(option, values));
            case "--args" -> reset
                    ? resetOnly(option, values, new Command.ConfigResetArgs())
                    : new Command.ConfigSetArgs(requireValues(option, values));
            case "--main" -> reset
                    ? resetOnly(option, values, new Command.ConfigResetMain())
                    : new Command.ConfigSetMain(requireSingle(option, values));
            default -> throw new IllegalStateException(
                    "Unhandled option: " + option
            );
        };
    }

    /*
     * Splits tokens into options and their values. A value list runs until
     * the next known option, so values such as "-v" are kept as values.
     */
    private static Map<String, List<String>> readOptions(String command,
                                                         List<String> tokens,
                                                         Set<String> known) {
        /*
         * The rule is simple: every term is either an option or a value.
         *
         * Is the term in the list of allowed options? An empty list is opened for it.
         * If that option has already appeared, it is a "duplicate" error.
         * Is the term not an option, and no option has been opened yet? It is an "unknown option" error.
         * Otherwise, the term is the value for the current option.
         *
         * input
         * ["--input", "a", "b", "--args", "-v"]
         *
         * result
         * { "--input": ["a","b"], "--args": ["-v"] }
         */
        Map<String, List<String>> options = new LinkedHashMap<>();
        List<String> current = null;

        for (String token : tokens) {
            if (known.contains(token)) {
                if (options.containsKey(token))
                    throw CommandSyntaxException.duplicateOption(
                            command, token
                    );

                current = new ArrayList<>();
                options.put(token, current);
            }
            else if (current == null) {
                throw CommandSyntaxException.unknownOption(
                        command, token
                );
            }
            else {
                current.add(token);
            }
        }
        return options;
    }

    private static List<String> values(String command,
                                       String option,
                                       Map<String, List<String>> options) {
        List<String> values = options.get(option);

        if (values == null)
            return List.of();

        if (values.isEmpty())
            throw CommandSyntaxException.missingValue(
                    command, option
            );

        return values;
    }

    private static void requireFlag(String option, List<String> values, boolean reset) {
        if (reset || !values.isEmpty())
            throw CommandSyntaxException.invalidUsage(
                    "config",
                    option
                            + " takes no values and cannot be combined with --reset"
            );
    }

    private static Command resetOnly(String option, List<String> values, Command command) {
        if (!values.isEmpty())
            throw CommandSyntaxException.invalidUsage(
                    "config",
                    option
                            + " --reset takes no values"
            );

        return command;
    }

    private static List<String> requireValues(String option, List<String> values) {
        if (values.isEmpty())
            throw CommandSyntaxException.missingValue(
                    "config", option
            );

        return values;
    }

    private static String requireSingle(String option, List<String> values) {
        if (values.size() != 1)
            throw CommandSyntaxException.invalidUsage(
                    "config", option + " needs exactly one value"
            );

        return values.getFirst();
    }
}