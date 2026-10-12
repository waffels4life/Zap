package com.zap;

import com.zap.cli.CommandParser;
import com.zap.project.ProjectLocator;
import com.zap.project.SourceDiscovery;

import java.io.IOException;
import java.nio.file.Path;

public final class Zap {

    private Zap() {}

    private static final String VERSION = "0.1.0";

    public static void main(String[] args) {

        CommandParser commandParser = new CommandParser();
        CommandParser.Command command = commandParser.parse(args);

        switch (command) {

            case RUN -> inspectProject();

            case HELP -> printHelp();

            case VERSION -> System.out.println(
                    "Zap " + VERSION
            );

            case UNKNOWN -> {
                System.err.println(
                        "Error: Unknown command."
                );
                printHelp();
                System.exit(2);
            }
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

    private static void inspectProject() {
        try {
            ProjectLocator locator = new ProjectLocator();
            Path projectRoot = locator.locate();

            Path sourceDirectory = projectRoot.resolve("src");

            SourceDiscovery discovery = new SourceDiscovery();

            System.out.println("Project: " + projectRoot);
            System.out.println("Java sources:");

            for (Path source : discovery.discover(sourceDirectory))
                System.out.println("  " + source);

        }
        catch (IOException | IllegalArgumentException e) {
            System.err.println("Error: " + e.getMessage());
            System.exit(1);
        }
    }
}