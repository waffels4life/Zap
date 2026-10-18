package com.zap.cli;

import com.zap.config.ConfigKeys;
import com.zap.config.ConfigManager;
import com.zap.config.ZapConfig;
import com.zap.project.ProjectInitializer;
import com.zap.project.ProjectLayout;
import com.zap.project.ProjectLocator;
import com.zap.project.mainclass.MainClassSourceLocator;

import java.io.PrintStream;
import java.nio.file.Path;
import java.util.Objects;

public final class InitCommandHandler {

    private final ProjectLocator locator;
    private final ConfigManager manager;
    private final MainClassSourceLocator sourceLocator;
    private final ProjectInitializer initializer;
    private final PrintStream out;

    public InitCommandHandler(ProjectLocator locator,
                              ConfigManager manager,
                              MainClassSourceLocator sourceLocator,
                              ProjectInitializer initializer,
                              PrintStream out) {
        this.locator = Objects.requireNonNull(locator, "locator");
        this.manager = Objects.requireNonNull(manager, "manager");
        this.sourceLocator = Objects.requireNonNull(sourceLocator, "sourceLocator");
        this.initializer = Objects.requireNonNull(initializer, "initializer");
        this.out = Objects.requireNonNull(out, "out");
    }

    public void handle(Command.Init init) {
        Path root = locator.currentDirectory();
        ZapConfig config = ZapConfig.defaults().withMainClass(init.mainClass());

        manager.requireValid(config);

        if (!init.mainClass().equals(ConfigKeys.AUTO_MAIN_CLASS)) {
            Path sources = ProjectLayout.of(root, config).sourceDirectory();
            sourceLocator.requireExists(sources, init.mainClass());
        }

        ProjectInitializer.Result result = initializer.initialize(root, config);

        out.println("Zap initialized successfully.");
        out.println("Configuration: " + result.configFile());
        out.println("Main class: " + config.mainClass());
        out.println("Run `zap help` to see available commands.");
    }
}