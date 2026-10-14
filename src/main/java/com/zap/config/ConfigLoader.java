package com.zap.config;

import com.zap.exception.ConfigIncorrectFormatException;
import com.zap.exception.ConfigIncorrectValuesException;
import com.zap.exception.ConfigMissingException;
import com.zap.exception.ConfigNotReadableException;
import com.zap.exception.ConfigurationException;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.MalformedInputException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.Properties;

public final class ConfigLoader {

    private static final String CONFIG_DIRECTORY = ".zap";
    private static final String CONFIG_FILE = "config.properties";

    public Path getConfigPath(Path projectRoot) {
        Objects.requireNonNull(projectRoot, "projectRoot");
        return projectRoot
                .resolve(CONFIG_DIRECTORY)
                .resolve(CONFIG_FILE);
    }

    public boolean exists(Path projectRoot) {
        return Files.isRegularFile(getConfigPath(projectRoot));
    }

    public ZapConfig load(Path projectRoot) throws ConfigurationException {

        Objects.requireNonNull(projectRoot, "projectRoot");
        Path configPath = getConfigPath(projectRoot);
        loadValidator(configPath);

        Properties properties = new Properties();
        try (Reader reader = Files.newBufferedReader(
                configPath,
                StandardCharsets.UTF_8)) {
            properties.load(reader);
        }
        catch (MalformedInputException | IllegalArgumentException e) {
            throw new ConfigIncorrectFormatException(
                    "Invalid config format: " + configPath, e
            );
        }
        catch (IOException e) {
            throw new ConfigNotReadableException(
                    "Cannot read config: " + configPath, e
            );
        }

        ZapConfig defaults = ZapConfig.defaults();
        return new ZapConfig(
                getValue(properties, "source.directory", defaults.sourceDirectory()),
                getValue(properties, "compiler.executable", defaults.compilerExecutable()),
                getValue(properties, "output.directory", defaults.outputDirectory()),
                getValue(properties, "runtime.executable", defaults.runtimeExecutable()),
                getValue(properties, "main.class", defaults.mainClass())
        );
    }

    private static void loadValidator(Path configPath) {
        if (!Files.exists(configPath))
            throw new ConfigMissingException(
                    "Config file does not exist: " + configPath
            );

        if (!Files.isRegularFile(configPath))
            throw new ConfigNotReadableException(
                    "Config path is not a regular file: " + configPath
            );


        if (!Files.isReadable(configPath))
            throw new ConfigNotReadableException(
                    "Config file is not readable: " + configPath
            );
    }

    public void save(Path projectRoot,
                     ZapConfig config) throws IOException {

        Objects.requireNonNull(config, "config");

        Properties properties = new Properties();
        properties.setProperty("source.directory", config.sourceDirectory());
        properties.setProperty("compiler.executable", config.compilerExecutable());
        properties.setProperty("output.directory", config.outputDirectory());
        properties.setProperty("runtime.executable", config.runtimeExecutable());
        properties.setProperty("main.class", config.mainClass());

        Path configPath = getConfigPath(projectRoot);
        Path dir = configPath.getParent();
        Files.createDirectories(dir);
        Path tmp = Files.createTempFile(dir, "config-", ".tmp");

        try {
            try (Writer writer = Files.newBufferedWriter(
                    tmp, StandardCharsets.UTF_8
            )) {
                properties.store(writer, "Zap project configuration");
            }
            try {
                Files.move(tmp, configPath, StandardCopyOption.ATOMIC_MOVE);
            }
            catch (AtomicMoveNotSupportedException e) {
                Files.move(tmp, configPath, StandardCopyOption.REPLACE_EXISTING);
            }
        }
        finally {
            Files.deleteIfExists(tmp);
        }
    }

    private static String getValue(Properties properties,
                                    String key,
                                    String defaultValue)
            throws ConfigIncorrectValuesException {

        Objects.requireNonNull(properties, "properties");
        String raw = properties.getProperty(key);

        if (raw == null) return defaultValue;
        String value = raw.trim();

        if (value.isEmpty())
            throw new ConfigIncorrectValuesException(
                    key + " must not be blank."
            );

        return value;
    }
}
