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

        /*
         * here we collect a stream of paths. streams are not lists; so it does not keep
         * the items in memory and creates each item on at a time.
         *
         * first filter >> only paths that are regular files can pass.
         */
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
