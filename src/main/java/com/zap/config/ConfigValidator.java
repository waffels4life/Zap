package com.zap.config;

import com.zap.exception.config.ConfigViolation;

import javax.lang.model.SourceVersion;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

public final class ConfigValidator {

    private static final List<String> REQUIRED_KEYS = List.of(
            ConfigKeys.SOURCE_DIRECTORY,
            ConfigKeys.COMPILER_EXECUTABLE,
            ConfigKeys.OUTPUT_DIRECTORY,
            ConfigKeys.RUNTIME_EXECUTABLE,
            ConfigKeys.MAIN_CLASS
    );

    public List<ConfigViolation> validate(Properties properties) {
        Objects.requireNonNull(properties, "properties must not be null");

        List<ConfigViolation> violations = new ArrayList<>();

        for (String key : REQUIRED_KEYS) {
            String value = properties.getProperty(key);

            if (value == null) {
                violations.add(new ConfigViolation(
                        key, "Missing required configuration key: " + key));
            } else if (value.isBlank()) {
                violations.add(new ConfigViolation(
                        key, "Configuration key has a blank value: " + key));
            } else if (key.equals(ConfigKeys.MAIN_CLASS)) {
                validateMainClass(value, violations);
            }
        }

        validatePath(properties, ConfigKeys.SOURCE_DIRECTORY, violations);
        validatePath(properties, ConfigKeys.OUTPUT_DIRECTORY, violations);
        validateIndexed(properties, ConfigKeys.INPUT_PREFIX, violations);
        validateIndexed(properties, ConfigKeys.ARGS_PREFIX, violations);

        return List.copyOf(violations);
    }

    private static void validateMainClass(String value, List<ConfigViolation> violations) {
        if (value.equals(ConfigKeys.AUTO_MAIN_CLASS)) {
            return;
        }

        if (!SourceVersion.isName(value)) {
            violations.add(new ConfigViolation(
                    ConfigKeys.MAIN_CLASS,
                    "Main class must be a fully qualified class name "
                            + "such as com.example.Main, but was: [" + value + "]"));
        }
    }

    private static void validatePath(Properties properties,
                                     String key,
                                     List<ConfigViolation> violations) {
        String value = properties.getProperty(key);
        if (value == null || value.isBlank()) {
            return;
        }
        try {
            Path.of(value.trim());
        }
        catch (InvalidPathException e) {
            violations.add(new ConfigViolation(
                    key, "Invalid path for " + key + ": " + value));
        }
    }

    private static void validateIndexed(Properties properties,
                                        String prefix,
                                        List<ConfigViolation> violations) {
        Set<Integer> indices = new TreeSet<>();

        for (String name : properties.stringPropertyNames()) {
            if (!name.startsWith(prefix)) {
                continue;
            }
            Integer index = parseIndex(name.substring(prefix.length()));
            if (index == null) {
                violations.add(new ConfigViolation(name,
                        "Invalid key: " + name + " (expected " + prefix + "<number>)"));
            } else {
                indices.add(index);
            }
        }

        int expected = 0;
        for (int index : indices) {
            if (index != expected) {
                violations.add(new ConfigViolation(prefix + expected,
                        "Missing key: " + prefix + expected
                                + " (numbered keys must be continuous from 0)"));
                return;
            }
            expected++;
        }
    }

    private static Integer parseIndex(String text) {
        boolean badLength = text.isEmpty() || text.length() > 6;
        boolean leadingZero = text.length() > 1 && text.charAt(0) == '0';

        if (badLength || leadingZero) {
            return null;
        }
        for (int i = 0; i < text.length(); i++) {
            if (!Character.isDigit(text.charAt(i))) {
                return null;
            }
        }
        return Integer.parseInt(text);
    }
}