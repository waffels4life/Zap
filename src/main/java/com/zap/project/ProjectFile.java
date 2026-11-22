package com.zap.project;

import java.nio.file.Path;
import java.util.Objects;

public record ProjectFile(Path relativePath, long sizeBytes) {

    public ProjectFile {
        Objects.requireNonNull(relativePath, "relativePath");

        if (relativePath.isAbsolute())
            throw new IllegalArgumentException("relativePath must be relative");

        if (sizeBytes < 0)
            throw new IllegalArgumentException("sizeBytes must not be negative");
    }
}
