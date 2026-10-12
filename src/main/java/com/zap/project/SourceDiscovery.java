package com.zap.project;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public final class SourceDiscovery {

    public List<Path> discover(Path sourceDirectory) throws IOException {

        Objects.requireNonNull(sourceDirectory);
        if (!Files.isDirectory(sourceDirectory))
            throw new IllegalStateException(
                    "Source directory does not exist: "
                            + sourceDirectory
            );

        try (Stream<Path> paths = Files.walk(sourceDirectory)) {
            Objects.requireNonNull(paths);
            return paths
                    .filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".java"))
                    .sorted()
                    .toList();
        }
    }
}
