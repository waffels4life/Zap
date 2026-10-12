package com.zap.project;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * Finds all regular .java files under the given directory, recursively.
 * Result is sorted and contains absolute, normalized paths.
 * Symbolic links are not followed.
 */
public final class SourceDiscovery {

    public List<Path> discover(Path sourceDirectory) throws IOException {

        Objects.requireNonNull(sourceDirectory, "sourceDirectory");

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
        try (Stream<Path> paths = Files.find(
                sourceDirectory,
                Integer.MAX_VALUE,
                (path, basicFileAttributes) ->
                        basicFileAttributes.isRegularFile() && isJavaSource(path)
        )) {
            return Objects.requireNonNull(paths.sorted().toList());
        }
    }

    private static boolean isJavaSource(Path path) {
        Objects.requireNonNull(path, "path");
        return path.getFileName().toString().endsWith(".java");
    }
}
