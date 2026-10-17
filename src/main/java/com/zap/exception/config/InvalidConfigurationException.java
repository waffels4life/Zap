package com.zap.exception.config;

import com.zap.config.ConfigValidator.ConfigViolation;
import com.zap.exception.ZapException;

import java.util.List;

public class InvalidConfigurationException extends ZapException {

    private final List<ConfigViolation> violations;

    public InvalidConfigurationException(List<ConfigViolation> violations) {
        super(buildMessage(violations));
        this.violations = List.copyOf(violations);
    }

    public List<ConfigViolation> getViolations() {
        return violations;
    }

    private static String buildMessage(List<ConfigViolation> violations) {
        StringBuilder message = new StringBuilder(
                "Configuration validation failed:"
        );

        for (ConfigViolation violation : violations)
            message.append(System.lineSeparator())
                    .append("- ")
                    .append(violation.key())
                    .append(": ")
                    .append(violation.message());


        return message.toString();
    }
}