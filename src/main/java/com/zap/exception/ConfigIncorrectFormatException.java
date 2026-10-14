package com.zap.exception;

/**
 * throws if configuration file format is incorrect.
 */
public class ConfigIncorrectFormatException extends ConfigurationException {
    public ConfigIncorrectFormatException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
