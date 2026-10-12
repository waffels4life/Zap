package com.zap;

import com.zap.cli.CommandParser;
import com.zap.execution.ProjectExecutor;

import java.io.IOException;

public final class Zap {

    private static final String VERSION = "0.1.0";

    private Zap() {}

    public static void main(String[] args) {
        CommandParser commandParser = new CommandParser();
        CommandParser.Command command = commandParser.parse(args);

        switch (command) {
            case RUN -> runProject();
            case HELP -> printHelp();
            case VERSION -> System.out.println("Zap " + VERSION);
            case UNKNOWN -> {
                System.err.println("Error: Unknown command.");
                printHelp();
                System.exit(2);
            }
        }
    }

    private static void runProject() {
        ProjectExecutor executor = new ProjectExecutor();

        try {
            int exitCode = executor.execute();

            if (exitCode != 0) System.exit(exitCode);
        }
        catch (IOException e) {
            System.err.println("I/O error: " + e.getMessage());
            System.exit(1);
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Execution interrupted.");
            System.exit(130);
        }
    }

    private static void printHelp() {
        System.out.println("""
                Zap - Fast Java project runner

                Usage:
                  zap run       Compile and run a Java project
                  zap help      Show this help message
                  zap version   Show the current version
                """);
    }
}