package com.zap.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.regex.Pattern;

public final class ConfigValidator {

    private static final List<String> REQUIRED_KEYS  = List.of(
            ConfigKeys.SOURCE_DIRECTORY,
            ConfigKeys.COMPILER_EXECUTABLE,
            ConfigKeys.OUTPUT_DIRECTORY,
            ConfigKeys.RUNTIME_EXECUTABLE,
            ConfigKeys.MAIN_CLASS
    );
    private static final Pattern VALID_CLASS_NAME = Pattern.compile(
            "^[a-zA-Z_$][a-zA-Z0-9_$]*(\\.[a-zA-Z_$][a-zA-Z0-9_$]*)*$"
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

        String mainClass = properties.getProperty(ConfigKeys.MAIN_CLASS);
        if (mainClass != null
                && !mainClass.isBlank()
                && !mainClass.equals("auto"))
            validateMainClass(mainClass, violations);

        return List.copyOf(violations);
    }

    private void validateMainClass(String mainClass, List<ConfigViolation> violations) {
        Objects.requireNonNull(mainClass, "mainClass");

        /*
         * To prevent invalid characters & structure
         */
        if (!VALID_CLASS_NAME.matcher(mainClass).matches())
            violations.add(new ConfigViolation(
                    ConfigKeys.MAIN_CLASS,
                    "Incorrect package format: invalid characters or structure ["
                    + mainClass
                    + "]"
            ));
    }
}
