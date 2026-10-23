package com.zap;

import com.zap.cli.Command;
import com.zap.cli.CommandParser;
import com.zap.cli.ConfigCommandHandler;
import com.zap.cli.HelpCommandHandler;
import com.zap.cli.InitCommandHandler;
import com.zap.cli.help.HelpCatalog;
import com.zap.cli.help.HelpFormatter;
import com.zap.config.ConfigLoader;
import com.zap.config.ConfigManager;
import com.zap.config.ConfigValidator;
import com.zap.config.ConfigWriter;
import com.zap.config.ZapConfig;
import com.zap.exception.ZapException;
import com.zap.exception.cli.CommandSyntaxException;
import com.zap.exception.execution.ExecutionInterruptedException;
import com.zap.execution.CompilerRunner;
import com.zap.execution.OutputCleaner;
import com.zap.execution.ProcessRunner;
import com.zap.execution.ProgramRunner;
import com.zap.execution.ProjectExecutor;
import com.zap.execution.RunOptions;
import com.zap.project.ProjectInitializer;
import com.zap.project.ProjectLayout;
import com.zap.project.ProjectLocator;
import com.zap.project.SourceDiscovery;
import com.zap.project.mainclass.MainClassFinder;
import com.zap.project.mainclass.MainClassResolver;
import com.zap.project.mainclass.MainClassSourceLocator;
import com.zap.project.mainclass.MainClassVerifier;

import java.nio.file.Path;

public final class Zap {

    private static final String VERSION = "1.1.0 SNAPSHOT";

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
        catch (CommandSyntaxException e) {
            System.err.println("Error: " + e.getMessage());
            exitCode = EXIT_MISUSE;
        }
        catch (ZapException e) {
            System.err.println("Error: " + e.getMessage());
            exitCode = EXIT_ERROR;
        }

        System.exit(exitCode);
    }

    private static int dispatch(String[] args) {
        Command command = new CommandParser().parse(args);

        return switch (command) {
            case Command.Init init -> {
                createInitHandler().handle(init);
                yield EXIT_OK;
            }
            case Command.Run run -> runProject(run);
            case Command.ConfigCommand config -> {
                createConfigHandler().handle(config);
                yield EXIT_OK;
            }
            case Command.Help help -> {
                createHelpHandler().handle(help);
                yield EXIT_OK;
            }
            case Command.HelpTopic topic -> {
                createHelpHandler().handle(topic);
                yield EXIT_OK;
            }
            case Command.Version version -> {
                System.out.println("Zap " + VERSION);
                yield EXIT_OK;
            }
        };
    }

    private static int runProject(Command.Run run) {
        ProjectLocator locator = new ProjectLocator();
        Path root = locator.locate(locator.currentDirectory());

        ZapConfig config = createManager().resolve(root);
        ProjectLayout layout = ProjectLayout.of(root, config);

        RunOptions options = new RunOptions(
                run.inputs().isEmpty()
                        ? config.inputs()
                        : run.inputs(),
                run.args().isEmpty()
                        ? config.args()
                        : run.args()
        );

        return createExecutor().execute(layout, config, options);
    }

    private static int notAvailable(String what) {
        System.err.println("Error: `" + what + "` is not available yet.");
        return EXIT_ERROR;
    }

    private static ConfigManager createManager() {
        return new ConfigManager(
                new ConfigLoader(),
                new ConfigValidator(),
                new ConfigWriter()
        );
    }

    private static InitCommandHandler createInitHandler() {
        return new InitCommandHandler(
                new ProjectLocator(),
                createManager(),
                new MainClassSourceLocator(),
                new ProjectInitializer(new ConfigWriter()),
                System.out);
    }

    private static ConfigCommandHandler createConfigHandler() {
        return new ConfigCommandHandler(
                new ProjectLocator(),
                createManager(),
                new MainClassSourceLocator(),
                System.out);
    }

    private static HelpCommandHandler createHelpHandler() {
        return new HelpCommandHandler(
                HelpCatalog.standard(),
                new HelpFormatter(),
                System.out);
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
                  init [auto | <package.Class>]         Initialize Zap in the current project
                  run, -r [--input ...] [--args ...]    Compile and run the project
                  config <option>                       Change the saved settings
                  help [<command>]                      Show help
                  version                               Show the version
                """);
    }
}