package com.zap.config;

import java.nio.file.Path;
import java.util.Objects;

public record ZapConfig(Path sourceDirectory,
                        String compilerExecutable,
                        Path outputDirectory,
                        String runtimeExecutable,
                        String mainClass) {

    public ZapConfig {

        Objects.requireNonNull(sourceDirectory, ConfigKeys.SOURCE_DIRECTORY);
        Objects.requireNonNull(outputDirectory, ConfigKeys.OUTPUT_DIRECTORY);

        compilerExecutable = requireNonBlank(
                compilerExecutable, ConfigKeys.COMPILER_EXECUTABLE
        );

        runtimeExecutable = requireNonBlank(
                runtimeExecutable, ConfigKeys.RUNTIME_EXECUTABLE
        );

        mainClass = requireNonBlank(
                mainClass, ConfigKeys.MAIN_CLASS
        );
    }

    public static ZapConfig defaults() {
        return new ZapConfig(
                Path.of("src/main/java"),
                "javac",
                Path.of(".zap/classes"),
                "java",
                ConfigKeys.AUTO_MAIN_CLASS
        );
    }

    private static String requireNonBlank(String value, String key) {
        if (value == null || value.isBlank())
            throw new IllegalArgumentException(
                    "Configuration value must not be blank: " + key
            );

        return value.trim();
    }
}
