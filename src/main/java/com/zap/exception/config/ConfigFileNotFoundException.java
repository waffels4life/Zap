package com.zap.exception.config;

import java.nio.file.Path;

public class ConfigFileNotFoundException extends ConfigurationException {
    public ConfigFileNotFoundException(Path file) {
        super("Config file not found: " + file, null);
    }
}
