package com.zap.exception.config;

/**
 * throws if configuration file format is incorrect.
 */
public class ConfigFormatException extends ConfigurationException {

    public ConfigFormatException(String message) {
        super(message);
    }

    public ConfigFormatException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
