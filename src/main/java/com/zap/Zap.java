package com.zap;

import com.zap.cli.CommandParser;
import com.zap.config.ConfigLoader;
import com.zap.config.ZapConfig;
import com.zap.execution.ProjectExecutor;

import java.io.IOException;
import java.nio.file.Path;

public final class Zap {

    private static final String VERSION = "0.1.0";

    /*
     * instance creation for Zap is forbidden.
     */
    private Zap() {}

    /**
     * this method operates the entire program form start till end.
     * @param args arguments given by the user when running the program
     */
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
                /*
                 * Java ends the process with code [2]
                 * any other code rather than [0] is considered a failure.
                 */
                System.exit(2);
            }
        }
    }

    /**
     * Connecting CLI to executer engine
     */
    private static void runProject() {
        ProjectExecutor executor = new ProjectExecutor();

        try {
            // return a number to determine if operation was successfully done [0].
            int exitCode = executor.execute();

            // if there were any code rather than [0], exit with that code.
            if (exitCode != 0) System.exit(exitCode);
        }
        /*
         * Code     Meaning
         * 0        Success
         * 1        General Error
         * 2        Misuse of the command
         * 130      The program was interrupted while running.
         */
        catch (IllegalStateException e) {
            System.err.println("Error: " + e.getMessage());
            System.exit(1);
        } // exception for not finding Main.java.

        catch (IOException e) {
            System.err.println("I/O error: " + e.getMessage());
            System.exit(1);
        } // exception for I/O problems.

        catch (InterruptedException e) {

            // Restore the interrupt status so other code can detect the interruption.
            Thread.currentThread().interrupt();

            System.err.println("Execution interrupted.");

            // Exit with a code indicating that execution was interrupted.
            System.exit(130);
        }
    }

    private static void printHelp() {

        // TODO separate with config

        System.out.println("""
            Zap - Fast Java project runner

            Usage:
              zap <command>

            Commands:
              run       Compile and run the Java project
              help      Show available commands
              version   Show the current version

            Examples:
              zap run
              zap help
              zap version
            """);
    }
}