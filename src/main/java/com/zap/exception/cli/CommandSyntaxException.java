package com.zap.exception.cli;

import com.zap.exception.ZapException;

public final class CommandSyntaxException extends ZapException {

    private CommandSyntaxException(String message) {
        super(message);
    }

    public static CommandSyntaxException unknownCommand(String name) {
        return new CommandSyntaxException(
                "Unknown command: " + name + ". Run `zap help`.");
    }

    public static CommandSyntaxException unknownOption(String command, String token) {
        String kind = token.startsWith("-") ? "option" : "argument";
        return new CommandSyntaxException(
                "Unexpected " + kind + " for `zap " + command + "`: " + token
                        + ". Run `zap help " + command + "`.");
    }

    public static CommandSyntaxException duplicateOption(String command, String option) {
        return new CommandSyntaxException(
                "Option " + option + " was given more than once for `zap "
                        + command + "`.");
    }

    public static CommandSyntaxException missingValue(String command, String option) {
        return new CommandSyntaxException(
                "Option " + option + " needs at least one value. Run `zap help "
                        + command + "`.");
    }

    public static CommandSyntaxException invalidUsage(String command, String detail) {
        return new CommandSyntaxException(
                "Invalid usage of `zap " + command + "`: " + detail
                        + ". Run `zap help " + command + "`.");
    }
}