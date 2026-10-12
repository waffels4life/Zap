package com.zap.project;

import java.nio.file.Files;
import java.nio.file.Path;

public final class ProjectLocator {

    private static final String CONFIG_FILE = "zap.properties";

    public Path locate() {
        Path workingDirectory = Path.of(System.getProperty("user.dir"))
                .toAbsolutePath()
                .normalize();

        if (!Files.isDirectory(workingDirectory))
            throw new IllegalStateException(
                    "Working directory does not exist: "
                            + workingDirectory
            );

        return workingDirectory;
    }
}
