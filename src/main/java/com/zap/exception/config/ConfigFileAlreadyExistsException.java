package com.zap.exception.config;

import com.zap.exception.ZapException;

import java.nio.file.Path;

public class ConfigFileAlreadyExistsException extends ZapException {
    public ConfigFileAlreadyExistsException(Path file) {
        super("Config file already exists: " + file);
    }
}
