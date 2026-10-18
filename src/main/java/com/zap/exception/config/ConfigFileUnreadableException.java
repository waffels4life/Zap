package com.zap.exception.config;

import java.nio.file.Path;

public final class ConfigFileUnreadableException extends ConfigurationException {

    public ConfigFileUnreadableException(Path file, Throwable cause) {
        super("Cannot read config file: " + file, cause);
    }
}