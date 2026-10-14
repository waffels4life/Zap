package com.zap.exception.config;

/**
 * throws if configuration file is not readable.
 */
public class ConfigReadabilityException extends ConfigurationException {

    public ConfigReadabilityException(String message) {
        super(message);
    }

    public ConfigReadabilityException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
