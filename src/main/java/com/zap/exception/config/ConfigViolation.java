package com.zap.exception.config;

import java.util.Objects;

public record ConfigViolation(String key, String message) {

    public ConfigViolation {
        Objects.requireNonNull(key, "key");
        Objects.requireNonNull(message, "message");
    }
}
