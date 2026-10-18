package com.zap.exception.config;

import java.nio.file.Path;

public final class ConfigFileFormatException extends ConfigurationException {

    public ConfigFileFormatException(Path file, Throwable cause) {
        super("Config file has an invalid format: " + file, cause);
    }
}