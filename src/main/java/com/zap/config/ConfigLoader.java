package com.zap.config;

import com.zap.exception.ConfigIncorrectFormatException;
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
        // 2) format correct?
        //    Properties.load(...) throws IllegalArgumentException -> ConfigFormatException

        // 3) values correct?
        //    key "main" missing or blank  -> ConfigValueException
        //    Path.of(raw) may throw InvalidPathException -> catch it
        //    Files.isRegularFile(main) false -> ConfigValueException
        Objects.requireNonNull(projectRoot, "projectRoot");
        Path configPath = getConfigPath(projectRoot);

        if (!Files.exists(configPath)) {
            // missing file: the caller decides (see the flowchart)
        }

        if (!Files.isRegularFile(configPath)) {
            throw new ConfigNotReadableException(
                    "Config path is not a regular file: " + configPath
            );
        }

        if (!Files.isReadable(configPath)) {
            throw new ConfigNotReadableException(
                    "Config path is not a regular file: " + configPath
            );
        }

        Properties properties = new Properties();

        try (Reader reader = Files.newBufferedReader(configPath, StandardCharsets.UTF_8)) {
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
                getValue(properties, "source.directory", defaults.getSourceDirectory()),
                getValue(properties, "compiler.executable", defaults.getCompilerExecutable()),
                getValue(properties, "output.directory", defaults.getOutputDirectory()),
                getValue(properties, "runtime.executable", defaults.getRuntimeExecutable()),
                getValue(properties, "main.class", defaults.getMainClass())
        );
    }

    public void save(Path projectRoot, ZapConfig config) throws IOException {

        Properties properties = new Properties();

        properties.setProperty("source.directory", config.getSourceDirectory());
        properties.setProperty("compiler.executable", config.getCompilerExecutable());
        properties.setProperty("output.directory", config.getOutputDirectory());
        properties.setProperty("runtime.executable", config.getRuntimeExecutable());
        properties.setProperty("main.class", config.getMainClass());

        Path configPath = getConfigPath(projectRoot);
        Path tmp = Files.createTempFile(configPath.getParent(), "config,", ".tmp");
        Files.createDirectories(configPath.getParent());

        try {
            try (Writer writer = Files.newBufferedWriter(configPath)) {
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

    private String getValue(Properties properties,
                            String key,
                            String defaultValue) {
        Objects.requireNonNull(properties, "properties");
        return properties.getProperty(key, defaultValue);
    }
}
