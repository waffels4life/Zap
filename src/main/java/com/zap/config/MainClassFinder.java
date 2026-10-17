package com.zap.config;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public final class MainClassFinder {

    private static final String MAIN_FILE_NAME = "Main.java";
    private static final String JAVA_EXTENSION = ".java";

    public MainClassSearchResult find(Path sourceDirectory) {
        Objects.requireNonNull(sourceDirectory, "sourceDirectory");

        List<String> candidates;
        try (Stream<Path> files = Files.walk(sourceDirectory)) {
            candidates = files
                    .filter(Files::isRegularFile)
                    .filter(file -> file.getFileName().toString().equals(MAIN_FILE_NAME))
                    .map(file -> toClassName(sourceDirectory, file))
                    .sorted()
                    .toList();
        }
        catch (IOException e) {
            throw new UncheckedIOException(
                    "Cannot scan source directory: " + sourceDirectory, e
            );
        }

        return switch (candidates.size()) {
            case 0 -> new MainClassSearchResult.NotFound();
            case 1 -> new MainClassSearchResult.Found(candidates.getFirst());
            default -> new MainClassSearchResult.Ambiguous(candidates);
        };
    }

    private static String toClassName(Path root, Path file) {
        String relative = root.relativize(file).toString();
        String withoutExtension = relative.substring(
                0, relative.length() - JAVA_EXTENSION.length()
        );
        return withoutExtension.replace(root.getFileSystem().getSeparator(), ".");
    }
}