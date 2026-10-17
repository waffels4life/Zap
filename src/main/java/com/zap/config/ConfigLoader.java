package com.zap.config;

import com.zap.exception.config.ConfigFileNotFoundException;
import com.zap.exception.config.ConfigFileUnreadableException;
import com.zap.exception.config.ConfigFileFormatException;
import com.zap.exception.config.ConfigurationException;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.MalformedInputException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Properties;

/*
 * Retrieving raw data from a file
 * Reading properties and passing the data to the next stage
 */
public final class ConfigLoader {

    private static final String CONFIG_DIRECTORY = ".zap";
    private static final String CONFIG_FILE = "config.properties";

    public Path getConfigPath(Path projectRoot) {
        Objects.requireNonNull(projectRoot, "projectRoot");
        return projectRoot
                .resolve(CONFIG_DIRECTORY)
                .resolve(CONFIG_FILE);
    }

    public Properties load(Path configPath) throws ConfigurationException {
        Objects.requireNonNull(configPath, "configPath");

        if (Files.notExists(configPath))
            throw new ConfigFileNotFoundException(configPath);

        if (!Files.isRegularFile(configPath) || !Files.isReadable(configPath))
            throw new ConfigFileUnreadableException(configPath, null);

        Properties properties = new Properties();

        try (Reader reader = Files.newBufferedReader(
                configPath, StandardCharsets.UTF_8)
        ) {
            properties.load(reader);
            return Objects.requireNonNull(properties, "properties");
        }
        catch (MalformedInputException | IllegalArgumentException e) {
            throw new ConfigFileFormatException(configPath, e);
        }
        catch (IOException e) {
            throw new ConfigFileUnreadableException(configPath, e);
        }
    }
}
