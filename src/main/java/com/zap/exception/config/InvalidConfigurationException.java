package com.zap.exception.config;

import com.zap.config.ConfigValidator;

import java.util.List;

/**
 * Unique exception for configuration managing.
 */
public class InvalidConfigurationException extends ConfigurationException {

    private final List<ConfigValidator.ConfigViolation> violations;

    public InvalidConfigurationException(
            List<ConfigValidator.ConfigViolation> violations) {

        super("Configuration validation failed.");
        this.violations = List.copyOf(violations);
    }

    public List<ConfigValidator.ConfigViolation> getViolations() {
        return violations;
    }
}
