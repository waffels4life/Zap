package com.zap.config;

import java.util.Objects;

public record ZapConfig(String sourceDirectory,
                        String compilerExecutable,
                        String outputDirectory,
                        String runtimeExecutable,
                        String mainClass,
                        Mode mode) {

    public enum Mode {
        AUTO, CUSTOM
    }

    public ZapConfig(String sourceDirectory,
                     String compilerExecutable,
                     String outputDirectory,
                     String runtimeExecutable,
                     String mainClass,
                     Mode mode) {

        this.sourceDirectory = requireNonBlank(
                sourceDirectory, "source.directory"
        );
        this.compilerExecutable = requireNonBlank(
                compilerExecutable, "compiler.executable"
        );
        this.outputDirectory = requireNonBlank(
                outputDirectory, "output.directory"
        );
        this.runtimeExecutable = requireNonBlank(
                runtimeExecutable, "runtime.executable"
        );
        this.mainClass = requireNonBlank(
                mainClass, "auto"
        );
        this.mode = Objects.requireNonNull(
                mode, "mode"
        );
    }

    public static ZapConfig defaults() {
        return new ZapConfig(
                "src/main/java",
                "javac",
                ".zap/classes",
                "java",
                "Main.java",
                Mode.AUTO
        );
    }

    private static String requireNonBlank(String value,
                                          String propertyName) {

        if (value == null || value.isBlank())
            throw new IllegalArgumentException(
                    "Configuration value must not be blank: "
                            + propertyName
            );

        return value.trim();
    }
}
