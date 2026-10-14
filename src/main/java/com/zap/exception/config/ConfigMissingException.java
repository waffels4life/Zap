package com.zap.exception.config;

/**
 * throws if configuration file did not exist.
 */
public class ConfigMissingException extends ConfigurationException {

    public ConfigMissingException(String message) {
        super(message);
    }

    public ConfigMissingException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
