package com.zap.exception.config;

import java.nio.file.Path;

/**
 * throws if configuration file format is incorrect.
 */
public class ConfigFileFormatException extends ConfigurationException {

    public ConfigFileFormatException(String message) {
        super(message);
    }

    public ConfigFileFormatException(Path path, Throwable throwable) {
        super("Config content is invalid: " + path, throwable);
    }
}
