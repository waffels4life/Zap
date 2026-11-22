package com.zap.project;

import com.zap.exception.ZapIoException;
import com.zap.exception.project.ProjectStructureException;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * Finds regular files under a project's source directory, recursively.
 * Returned paths are relative to the source directory. Symbolic links are not followed.
 */
public final class ProjectFileScanner {

    public List<ProjectFile> scan(Path sourceDirectory) {
        Objects.requireNonNull(sourceDirectory, "sourceDirectory");

        Path root = sourceDirectory.toAbsolutePath().normalize();
        if (!Files.isDirectory(root))
            throw new ProjectStructureException(
                    "Source directory not found: " + root
            );

        List<ProjectFile> files = new ArrayList<>();

        try {
            Files.walkFileTree(root, new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult visitFile(
                        Path file,
                        BasicFileAttributes attributes) {
                    if (attributes.isRegularFile()) {
                        files.add(new ProjectFile(
                                root.relativize(file),
                                attributes.size()
                        ));
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        }
        catch (IOException e) {
            throw new ZapIoException(
                    "Cannot scan source directory: " + root, e
            );
        }

        return files.stream()
                .sorted(Comparator.comparing(file ->
                        file.relativePath().toString()))
                .toList();
    }
}
