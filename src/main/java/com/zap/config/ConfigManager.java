package com.zap.config;

import com.zap.exception.config.ConfigFileNotFoundException;
import com.zap.exception.config.ConfigViolation;
import com.zap.exception.config.InvalidConfigurationException;
import com.zap.exception.project.ProjectNotInitializedException;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.function.UnaryOperator;

public final class ConfigManager {

    private final ConfigLoader loader;
    private final ConfigValidator validator;
    private final ConfigWriter writer;

    public ConfigManager(ConfigLoader loader,
                         ConfigValidator validator,
                         ConfigWriter writer) {
        this.loader = Objects.requireNonNull(loader, "loader");
        this.validator = Objects.requireNonNull(validator, "validator");
        this.writer = Objects.requireNonNull(writer, "writer");
    }

    /** Reads the saved config. Throws if the project is not initialized or the config is invalid. */
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
        for (String name : user.stringPropertyNames()) {
            merged.setProperty(name, normalize(name, user.getProperty(name)));
        }

        List<ConfigViolation> violations = validator.validate(merged);
        if (!violations.isEmpty()) {
            throw new InvalidConfigurationException(violations);
        }

        return toConfig(merged);
    }

    /** Applies a change to the saved config, validates the result, then saves it. */
    public ZapConfig update(Path projectRoot, UnaryOperator<ZapConfig> change) {
        Objects.requireNonNull(change, "change");
        return save(projectRoot, change.apply(resolve(projectRoot)));
    }

    /** Validates the given config and replaces the saved one. */
    public ZapConfig save(Path projectRoot, ZapConfig config) {
        Objects.requireNonNull(projectRoot, "projectRoot");
        Objects.requireNonNull(config, "config");

        requireValid(config);
        writer.replace(ConfigPaths.configFile(projectRoot), config);
        return config;
    }

    /** Throws InvalidConfigurationException with every problem found. */
    public void requireValid(ZapConfig config) {
        Objects.requireNonNull(config, "config");

        Properties properties = new Properties();
        config.asMap().forEach(properties::setProperty);

        List<ConfigViolation> violations = validator.validate(properties);
        if (!violations.isEmpty()) {
            throw new InvalidConfigurationException(violations);
        }
    }

    private static Properties defaultProperties() {
        Properties properties = new Properties();
        ZapConfig.defaults().asMap().forEach(properties::setProperty);
        return properties;
    }

    /* Spaces matter inside inputs and arguments, so only other values are trimmed. */
    private static String normalize(String key, String value) {
        boolean significant = key.startsWith(ConfigKeys.INPUT_PREFIX)
                || key.startsWith(ConfigKeys.ARGS_PREFIX);
        return significant ? value : value.trim();
    }

    private static ZapConfig toConfig(Properties p) {
        return new ZapConfig(
                Path.of(p.getProperty(ConfigKeys.SOURCE_DIRECTORY)),
                p.getProperty(ConfigKeys.COMPILER_EXECUTABLE),
                Path.of(p.getProperty(ConfigKeys.OUTPUT_DIRECTORY)),
                p.getProperty(ConfigKeys.RUNTIME_EXECUTABLE),
                p.getProperty(ConfigKeys.MAIN_CLASS),
                indexed(p, ConfigKeys.INPUT_PREFIX),
                indexed(p, ConfigKeys.ARGS_PREFIX));
    }

    /* The validator already guarantees the keys are continuous from 0. */
    private static List<String> indexed(Properties p, String prefix) {
        List<String> values = new ArrayList<>();
        for (int i = 0; p.getProperty(prefix + i) != null; i++) {
            values.add(p.getProperty(prefix + i));
        }
        return values;
    }
}