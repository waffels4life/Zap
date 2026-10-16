package com.zap.exception.config;

/**
 * Unique exception for configuration managing.
 */
public class InvalidConfigurationException extends ConfigurationException {

    public InvalidConfigurationException(String message) {
        super(message);
    }

    public InvalidConfigurationException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
