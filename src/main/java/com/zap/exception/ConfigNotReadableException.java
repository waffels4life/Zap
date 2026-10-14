package com.zap.exception;

/**
 * throws if configuration file is not readable.
 */
public class ConfigNotReadableException extends ConfigurationException {

    public ConfigNotReadableException(String message) {
        super(message);
    }

    public ConfigNotReadableException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
