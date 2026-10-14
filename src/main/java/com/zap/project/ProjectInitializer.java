package com.zap.project;

import com.zap.config.ConfigLoader;
import com.zap.config.ZapConfig;

import java.io.IOException;
import java.nio.file.Path;

public final class ProjectInitializer {

    private final ConfigLoader configLoader;

    public ProjectInitializer() {
        this.configLoader = new ConfigLoader();
    }

    public void initialize(Path projectRoot) throws IOException {

        if (configLoader.exists(projectRoot))
            throw new IllegalStateException(
                    "Project is already initialized."
            );

        ZapConfig zapConfig = ZapConfig.defaults();
        configLoader.save(projectRoot, zapConfig);

        System.out.println("Zap initialized successfully.");
        System.out.println("Configuration: .zap/config.properties");
        System.out.println("Main class: " + zapConfig.mainClass());
        System.out.println("Run `zap help` to see available commands.");
    }
}
