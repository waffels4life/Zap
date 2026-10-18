package com.zap.exception.config;

import com.zap.exception.ZapException;

public class ConfigurationException extends ZapException {

    public ConfigurationException(String message) {
        super(message);
    }

    public ConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }
}