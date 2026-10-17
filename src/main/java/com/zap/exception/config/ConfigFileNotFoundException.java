package com.zap.exception.config;

import com.zap.exception.ZapException;

import java.nio.file.Path;

public final class ConfigFileNotFoundException extends ZapException {
    public ConfigFileNotFoundException(Path file) {
        super("Config file not found: " + file, null);
    }
}
