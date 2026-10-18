package com.zap.exception.config;

import java.util.List;
import java.util.stream.Collectors;

public final class InvalidConfigurationException extends ConfigurationException {

    private final List<ConfigViolation> violations;

    public InvalidConfigurationException(List<ConfigViolation> violations) {
        super(buildMessage(violations));
        this.violations = List.copyOf(violations);
    }

    public List<ConfigViolation> violations() {
        return violations;
    }

    private static String buildMessage(List<ConfigViolation> violations) {
        return violations.stream()
                .map(violation -> "- " + violation.message())
                .collect(Collectors.joining(
                        System.lineSeparator(),
                        "Invalid configuration:" + System.lineSeparator(),
                        ""));
    }
}