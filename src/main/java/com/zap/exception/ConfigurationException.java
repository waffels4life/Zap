package com.zap.exception;

/**
 * super class for all exceptions related to Configurations
 */
public class ConfigurationException extends RuntimeException {
    public ConfigurationException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
