package com.zap.project;

import com.zap.exception.ZapIoException;
import com.zap.exception.config.ProjectStructureException;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * Finds all regular .java files under a directory, recursively.
 * Returns absolute, normalized, sorted paths. Symbolic links are not followed.
 */
public final class SourceDiscovery {

    public List<Path> discover(Path sourceDirectory) {
        Objects.requireNonNull(sourceDirectory, "sourceDirectory");

        if (!Files.isDirectory(sourceDirectory)) {
            throw new ProjectStructureException(
                    "Source directory not found: " + sourceDirectory);
        }

        try (Stream<Path> paths = Files.find(
                sourceDirectory,
                Integer.MAX_VALUE,
                (path, attributes) -> attributes.isRegularFile() && isJavaSource(path))) {

            return paths
                    .map(path -> path.toAbsolutePath().normalize())
                    .sorted()
                    .toList();
        }
        catch (IOException | UncheckedIOException e) {
            throw new ZapIoException(
                    "Cannot scan source directory: " + sourceDirectory, e);
        }
    }

    private static boolean isJavaSource(Path path) {
        return path.getFileName().toString().endsWith(".java");
    }
}