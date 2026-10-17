package com.zap.exception.config;

import com.zap.exception.ZapException;

import java.nio.file.Path;
import java.util.List;

public final class MainClassException extends ZapException {

    private MainClassException(String message) {
        super(message);
    }

    public static MainClassException notFound(Path sourceDirectory) {
        return new MainClassException("No Main.java found in " + sourceDirectory
                + ". Use `zap config --main <package.Class>`.");
    }

    public static MainClassException ambiguous(List<String> candidates) {
        return new MainClassException("Multiple Main.java files found: "
                + String.join(", ", candidates)
                + ". Choose one with `zap config --main`.");
    }

    public static MainClassException invalid(String className, String reason) {
        return new MainClassException(
                "Cannot run " + className + ": " + reason
        );
    }
}