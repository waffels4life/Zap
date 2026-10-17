package com.zap.execution;

import com.zap.exception.ZapIoException;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Objects;

public final class OutputCleaner {

    public void clean(Path directory) {
        Objects.requireNonNull(directory, "directory");

        try {
            if (Files.notExists(directory)) {
                Files.createDirectories(directory);
                return;
            }

            Files.walkFileTree(directory, new SimpleFileVisitor<Path>() {
                @Override public FileVisitResult visitFile(Path file, BasicFileAttributes attributes)
                        throws IOException {
                    Files.delete(file);
                    return FileVisitResult.CONTINUE;
                }

                @Override public FileVisitResult postVisitDirectory(Path dir, IOException error)
                        throws IOException {
                    if (error != null) {
                        throw error;
                    }
                    if (!dir.equals(directory)) {
                        Files.delete(dir);
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        }
        catch (IOException e) {
            throw new ZapIoException(
                    "Cannot clean output directory: " + directory, e);
        }
    }
}