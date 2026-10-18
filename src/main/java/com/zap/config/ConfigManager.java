package com.zap.config;

import com.zap.exception.config.ConfigFileNotFoundException;
import com.zap.exception.config.InvalidConfigurationException;
import com.zap.exception.config.ProjectNotInitializedException;

import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

public final class ConfigManager {

    private final ConfigLoader loader;
    private final ConfigValidator validator;

    public ConfigManager(ConfigLoader loader, ConfigValidator validator) {
        this.loader = Objects.requireNonNull(loader, "loader");
        this.validator = Objects.requireNonNull(validator, "validator");
    }

    public ZapConfig resolve(Path projectRoot) {
        Objects.requireNonNull(projectRoot, "projectRoot");

        Properties user;
        try {
            user = loader.load(ConfigPaths.configFile(projectRoot));
        }
        catch (ConfigFileNotFoundException e) {
            throw new ProjectNotInitializedException(projectRoot);
        }

        Properties merged = new Properties(defaultProperties());

        for (String name : user.stringPropertyNames())
            merged.setProperty(name, user.getProperty(name));


        List<ConfigValidator.ConfigViolation> violations = validator.validate(merged);
        if (!violations.isEmpty())
            throw new InvalidConfigurationException(violations);


        return toConfig(merged);
    }

    private static Properties defaultProperties() {
        Properties properties = new Properties();
        ZapConfig.defaults().asMap().forEach(properties::setProperty);
        return properties;
    }

    private static ZapConfig toConfig(Properties properties) {
        return new ZapConfig(
                Path.of(value(properties, ConfigKeys.SOURCE_DIRECTORY)),
                value(properties, ConfigKeys.COMPILER_EXECUTABLE),
                Path.of(value(properties, ConfigKeys.OUTPUT_DIRECTORY)),
                value(properties, ConfigKeys.RUNTIME_EXECUTABLE),
                value(properties, ConfigKeys.MAIN_CLASS)
        );
    }

    private static String value(Properties properties, String key) {
        return properties.getProperty(key).trim();
    }
}