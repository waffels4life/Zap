package com.zap.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

public final class ConfigValidator {

    private static final List<String> REQUIRED_KEYS  = List.of(
            ConfigKeys.SOURCE_DIRECTORY,
            ConfigKeys.COMPILER_EXECUTABLE,
            ConfigKeys.OUTPUT_DIRECTORY,
            ConfigKeys.RUNTIME_EXECUTABLE,
            ConfigKeys.MAIN_CLASS
    );

    public record ConfigViolation(String key, String message) {}

    public List<ConfigViolation> validate(Properties properties) {

        Objects.requireNonNull(properties, "properties");
        List<ConfigViolation> violations = new ArrayList<>();

        for (String key : REQUIRED_KEYS) {
            String value = properties.getProperty(key);

            if (value == null)
                violations.add(new ConfigViolation(
                        key, "Missing required configuration key: " + key)
                );
            else if (value.isBlank())
                violations.add(new ConfigViolation(
                        key, "Configuration key has a blank value: " + key)
                );

        }

        return List.copyOf(violations);
    }
}
