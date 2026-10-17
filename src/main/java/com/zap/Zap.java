package com.zap;

import com.zap.cli.CommandParser;
import com.zap.config.ConfigLoader;
import com.zap.config.ConfigManager;
import com.zap.config.ConfigValidator;
import com.zap.config.ZapConfig;
import com.zap.exception.ZapException;
import com.zap.exception.execution.ExecutionInterruptedException;
import com.zap.execution.CompilerRunner;
import com.zap.execution.OutputCleaner;
import com.zap.execution.ProcessRunner;
import com.zap.execution.ProgramRunner;
import com.zap.execution.ProjectExecutor;
import com.zap.execution.RunOptions;
import com.zap.project.ProjectLayout;
import com.zap.project.ProjectLocator;
import com.zap.project.SourceDiscovery;
import com.zap.project.mainclass.MainClassFinder;
import com.zap.project.mainclass.MainClassResolver;
import com.zap.project.mainclass.MainClassVerifier;

import java.nio.file.Path;

public final class Zap {

    private static final String VERSION = "0.1.0";

    private static final int EXIT_OK = 0;
    private static final int EXIT_ERROR = 1;
    private static final int EXIT_MISUSE = 2;
    private static final int EXIT_INTERRUPTED = 130;

    private Zap() {}

    public static void main(String[] args) {
        int exitCode;

        try {
            exitCode = dispatch(args);
        }
        catch (ExecutionInterruptedException e) {
            System.err.println(e.getMessage());
            exitCode = EXIT_INTERRUPTED;
        }
        catch (ZapException e) {
            System.err.println("Error: " + e.getMessage());
            exitCode = EXIT_ERROR;
        }

        System.exit(exitCode);
    }

    private static int dispatch(String[] args) {
        CommandParser.Command command = new CommandParser().parse(args);

        return switch (command) {
            case INIT -> initializeProject();
            case RUN -> runProject();
            case HELP -> {
                printHelp();
                yield EXIT_OK;
            }
            case VERSION -> {
                System.out.println("Zap " + VERSION);
                yield EXIT_OK;
            }
            case UNKNOWN -> {
                System.err.println("Error: Unknown command.");
                printHelp();
                yield EXIT_MISUSE;
            }
        };
    }

    private static int initializeProject() {
        // TODO: connect ProjectInitializer after ConfigWriter is written.
        System.err.println("Error: `init` is not available yet.");
        return EXIT_ERROR;
    }

    private static int runProject() {
        ProjectLocator locator = new ProjectLocator();
        Path root = locator.locate(locator.currentDirectory());

        ZapConfig config = new ConfigManager(new ConfigLoader(), new ConfigValidator())
                .resolve(root);
        ProjectLayout layout = ProjectLayout.of(root, config);

        return createExecutor().execute(layout, config, RunOptions.none());
    }

    private static ProjectExecutor createExecutor() {
        ProcessRunner processRunner = new ProcessRunner();

        return new ProjectExecutor(
                new SourceDiscovery(),
                new MainClassResolver(new MainClassFinder()),
                new MainClassVerifier(),
                new OutputCleaner(),
                new CompilerRunner(processRunner),
                new ProgramRunner(processRunner)
        );
    }

    private static void printHelp() {
        // TODO: generate from the final command list.
        System.out.println("""
                Zap - Fast Java project runner

                Usage:
                  zap <command> [options]

                Commands:
                  init      Initialize Zap in the current project
                  run       Compile and run the Java project
                  help      Show available commands
                  version   Show the current version
                """);
    }
}