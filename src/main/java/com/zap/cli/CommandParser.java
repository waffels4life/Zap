package com.zap.cli;

import java.util.Objects;

public final class CommandParser {

    public enum Command {
        RUN,
        HELP,
        VERSION,
        UNKNOWN
    }

    public Command parse(String... args) {

        Objects.requireNonNull(args);

        if (args.length == 0) return Command.HELP;

        return switch (args[0]) {
            case "run", "--r" -> Command.RUN;
            case "help", "--h" -> Command.HELP;
            case "version", "--v" -> Command.VERSION;
            case null, default -> Command.UNKNOWN;
        };
    }
}
