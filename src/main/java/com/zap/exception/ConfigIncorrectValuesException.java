package com.zap.exception;

/**
 * throws if the configuration values are not adding up.
 */
public class ConfigIncorrectValuesException extends ConfigurationException {

    public ConfigIncorrectValuesException(String message) {
        super(message);
    }

    public ConfigIncorrectValuesException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
