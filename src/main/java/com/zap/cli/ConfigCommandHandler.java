package com.zap.cli;

import com.zap.config.ConfigKeys;
import com.zap.config.ConfigManager;
import com.zap.config.ZapConfig;
import com.zap.project.ProjectLayout;
import com.zap.project.ProjectLocator;
import com.zap.project.mainclass.MainClassSourceLocator;

import java.io.PrintStream;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

public final class ConfigCommandHandler {

    private final ProjectLocator locator;
    private final ConfigManager manager;
    private final MainClassSourceLocator sourceLocator;
    private final PrintStream out;

    public ConfigCommandHandler(ProjectLocator locator,
                                ConfigManager manager,
                                MainClassSourceLocator sourceLocator,
                                PrintStream out) {
        this.locator = Objects.requireNonNull(locator, "locator");
        this.manager = Objects.requireNonNull(manager, "manager");
        this.sourceLocator = Objects.requireNonNull(sourceLocator, "sourceLocator");
        this.out = Objects.requireNonNull(out, "out");
    }

    public void handle(Command.ConfigCommand command) {
        Path root = locator.locate(locator.currentDirectory());

        switch (command) {
            case Command.ConfigShow showCommand ->
                    show(manager.resolve(root));

            case Command.ConfigSetInput set -> {
                manager.update(root, config -> config.withInputs(set.values()));
                out.println("Saved " + set.values().size() + " input(s).");
            }
            case Command.ConfigResetInput resetInput -> {
                manager.update(root, config -> config.withInputs(List.of()));
                out.println("Inputs cleared.");
            }

            case Command.ConfigSetArgs set -> {
                manager.update(root, config -> config.withArgs(set.values()));
                out.println("Saved " + set.values().size() + " argument(s).");
            }
            case Command.ConfigResetArgs resetArgs -> {
                manager.update(root, config -> config.withArgs(List.of()));
                out.println("Arguments cleared.");
            }

            case Command.ConfigSetMain set -> {
                setMainClass(root, set.mainClass());
                out.println("Main class set to " + set.mainClass() + ".");
            }
            case Command.ConfigResetMain resetMain -> {
                manager.update(root,
                        config -> config.withMainClass(ConfigKeys.AUTO_MAIN_CLASS));
                out.println("Main class reset to auto.");
            }

            case Command.ConfigResetAll resetAll -> {
                manager.update(root, config -> ZapConfig.defaults());
                out.println("Configuration reset to defaults.");
            }
        }
    }

    private void setMainClass(Path root, String mainClass) {
        ZapConfig candidate = manager.resolve(root).withMainClass(mainClass);

        manager.requireValid(candidate);

        if (!mainClass.equals(ConfigKeys.AUTO_MAIN_CLASS)) {
            Path sources = ProjectLayout.of(root, candidate).sourceDirectory();
            sourceLocator.requireExists(sources, mainClass);
        }

        manager.save(root, candidate);
    }

    private void show(ZapConfig config) {
        config.asMap().forEach((key, value) -> out.println(key + "=" + value));

        if (config.inputs().isEmpty()) {
            out.println("input=(none)");
        }
        if (config.args().isEmpty()) {
            out.println("args=(none)");
        }
    }
}