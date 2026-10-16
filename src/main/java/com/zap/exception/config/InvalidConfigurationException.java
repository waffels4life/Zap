package com.zap.exception.config;

import com.zap.config.ConfigValidator;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Unique exception for configuration managing.
 */
public class InvalidConfigurationException extends ConfigurationException {

    private final List<ConfigValidator.ConfigViolation> violations;

    public InvalidConfigurationException(List<ConfigValidator.ConfigViolation> violations) {
        super(buildMessage(violations));
        this.violations = List.copyOf(violations);
    }

    public InvalidConfigurationException(String message, Throwable cause) {
        super(message, cause);
        this.violations = List.of();
    }

    public List<ConfigValidator.ConfigViolation> violations() {
        return violations;
    }

    private static String buildMessage(List<ConfigValidator.ConfigViolation> violations) {
        return violations.stream()
                .map(v -> "- " + v.message())
                .collect(Collectors.joining(
                        System.lineSeparator(),
                        "Invalid configuration:" + System.lineSeparator(),
                        ""));
    }
}
