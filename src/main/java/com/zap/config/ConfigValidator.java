package com.zap.config;

import javax.lang.model.SourceVersion;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

public final class ConfigValidator {

    private static final List<String> REQUIRED_KEYS = List.of(
            ConfigKeys.SOURCE_DIRECTORY,
            ConfigKeys.COMPILER_EXECUTABLE,
            ConfigKeys.OUTPUT_DIRECTORY,
            ConfigKeys.RUNTIME_EXECUTABLE,
            ConfigKeys.MAIN_CLASS
    );

    public record ConfigViolation(String key, String message) {

        public ConfigViolation {
            Objects.requireNonNull(key, "key");
            Objects.requireNonNull(message, "message");
        }
    }

    public List<ConfigViolation> validate(Properties properties) {
        Objects.requireNonNull(properties, "properties must not be null");

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

            else if (key.equals(ConfigKeys.MAIN_CLASS))
                validateMainClass(value, violations);

        }

        return List.copyOf(violations);
    }

    private static void validateMainClass(String value, List<ConfigViolation> violations) {
        if (value.equals(ConfigKeys.AUTO_MAIN_CLASS)) return;

        if (!SourceVersion.isName(value))
            violations.add(new ConfigViolation(
                    ConfigKeys.MAIN_CLASS,
                    "Main class must be a fully qualified class name "
                            + "such as com.example.Main, but was: [" + value + "]"));
    }
}