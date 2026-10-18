package com.zap.project.mainclass;

import com.zap.exception.project.MainClassException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public final class MainClassSourceLocator {

    public Path sourceFileOf(Path sourceDirectory, String className) {
        Objects.requireNonNull(sourceDirectory, "sourceDirectory");
        Objects.requireNonNull(className, "className");

        int nested = className.indexOf('$');
        String outer = nested < 0 ? className : className.substring(0, nested);

        return sourceDirectory.resolve(outer.replace('.', '/') + ".java");
    }

    public void requireExists(Path sourceDirectory, String className) {
        Path file = sourceFileOf(sourceDirectory, className);

        if (!Files.isRegularFile(file)) {
            throw MainClassException.sourceNotFound(className, file);
        }
    }
}