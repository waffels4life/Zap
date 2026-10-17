package com.zap.project;

import com.zap.config.ConfigPaths;
import com.zap.exception.config.ProjectNotInitializedException;
import com.zap.exception.config.ProjectStructureException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public final class ProjectLocator {

    public Path currentDirectory() {
        Path directory = Path.of(System.getProperty("user.dir"))
                .toAbsolutePath()
                .normalize();

        if (!Files.isDirectory(directory)) {
            throw new ProjectStructureException(
                    "Working directory does not exist: " + directory);
        }
        return directory;
    }

    public Path locate(Path start) {
        Objects.requireNonNull(start, "start");

        for (Path dir = start.toAbsolutePath().normalize();
             dir != null;
             dir = dir.getParent()) {

            if (Files.isRegularFile(ConfigPaths.configFile(dir)))
                return dir;

        }
        throw new ProjectNotInitializedException(start);
    }
}