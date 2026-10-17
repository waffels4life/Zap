package com.zap.config;

import java.nio.file.Path;
import java.util.Objects;

public final class ConfigPaths {

    private static final String CONFIG_DIRECTORY = ".zap";
    private static final String CONFIG_FILE = "config.properties";

    private ConfigPaths() {}

    public static Path configFile(Path projectRoot) {
        Objects.requireNonNull(projectRoot, "projectRoot");
        return projectRoot.resolve(CONFIG_DIRECTORY).resolve(CONFIG_FILE);
    }
}