package com.zap.config;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public final class ConfigLoader {

    private static final String CONFIG_DIRECTORY = ".zap";
    private static final String CONFIG_FILE = "config.properties";

    public Path getConfigPath(Path projectRoot) {
        return projectRoot
                .resolve(CONFIG_DIRECTORY)
                .resolve(CONFIG_FILE);
    }

    public boolean exists(Path projectRoot) {
        return Files.isRegularFile(getConfigPath(projectRoot));
    }

    public ZapConfig load(Path projectRoot) throws IOException {
        Path configPath = getConfigPath(projectRoot);

        if (!Files.isRegularFile(configPath)) {
            if (Files.exists(configPath)) {
                throw new IOException(
                        "Configuration path is not a regular file: "
                                + configPath
                );
            }

            return ZapConfig.defaults();
        }

        Properties properties = new Properties();

        try (Reader reader = Files.newBufferedReader(configPath)) {
            properties.load(reader);
        }

        ZapConfig defaults = ZapConfig.defaults();

        return new ZapConfig(
                getValue(properties, "source.directory",
                        defaults.getSourceDirectory()),
                getValue(properties, "compiler.executable",
                        defaults.getCompilerExecutable()),
                getValue(properties, "output.directory",
                        defaults.getOutputDirectory()),
                getValue(properties, "runtime.executable",
                        defaults.getRuntimeExecutable()),
                getValue(properties, "main.class",
                        defaults.getMainClass())
        );
    }

    public void save(Path projectRoot,
                     ZapConfig config) throws IOException {

        Path configPath = getConfigPath(projectRoot);

        Files.createDirectories(configPath.getParent());

        Properties properties = new Properties();

        properties.setProperty(
                "source.directory", config.getSourceDirectory());
        properties.setProperty(
                "compiler.executable", config.getCompilerExecutable());
        properties.setProperty(
                "output.directory", config.getOutputDirectory());
        properties.setProperty(
                "runtime.executable", config.getRuntimeExecutable());
        properties.setProperty(
                "main.class", config.getMainClass());

        try (Writer writer = Files.newBufferedWriter(configPath)) {
            properties.store(writer, "Zap project configuration");
        }
    }

    private String getValue(
            Properties properties,
            String key,
            String defaultValue
    ) {
        return properties.getProperty(key, defaultValue);
    }
}
