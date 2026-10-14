package com.zap.exception.config;

/**
 * throws if the configuration values are not adding up.
 */
public class ConfigValuesException extends ConfigurationException {

    public ConfigValuesException(String message) {
        super(message);
    }

    public ConfigValuesException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
