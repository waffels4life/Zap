package com.zap.project;

import com.zap.config.ConfigPaths;
import com.zap.config.ConfigWriter;
import com.zap.config.ZapConfig;
import com.zap.exception.config.ConfigFileAlreadyExistsException;
import com.zap.exception.project.AlreadyInitializedException;

import java.nio.file.Path;
import java.util.Objects;

public final class ProjectInitializer {

    public record Result(Path configFile, ZapConfig config) {}

    private final ConfigWriter configWriter;

    public ProjectInitializer(ConfigWriter configWriter) {
        this.configWriter = Objects.requireNonNull(configWriter, "configWriter");
    }

    public Result initialize(Path projectRoot, ZapConfig config) {
        Objects.requireNonNull(projectRoot, "projectRoot");
        Objects.requireNonNull(config, "config");

        Path file = ConfigPaths.configFile(projectRoot);

        try {
            configWriter.createNew(file, config);
        }
        catch (ConfigFileAlreadyExistsException e) {
            throw new AlreadyInitializedException(projectRoot);
        }

        return new Result(file, config);
    }
}