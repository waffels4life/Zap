package com.zap.config;

import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
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

    public Map<String, String> asMap() {
        Map<String, String> map = new LinkedHashMap<>();
        map.put(ConfigKeys.SOURCE_DIRECTORY, portable(sourceDirectory));
        map.put(ConfigKeys.COMPILER_EXECUTABLE, compilerExecutable);
        map.put(ConfigKeys.OUTPUT_DIRECTORY, portable(outputDirectory));
        map.put(ConfigKeys.RUNTIME_EXECUTABLE, runtimeExecutable);
        map.put(ConfigKeys.MAIN_CLASS, mainClass);
        return map;
    }

    private static String portable(Path path) {
        Objects.requireNonNull(path, "path");
        return path.toString().replace('\\', '/');
    }

    private static String requireNonBlank(String value, String key) {
        if (value == null || value.isBlank())
            throw new IllegalArgumentException(
                    "Configuration value must not be blank: " + key
            );

        return value.trim();
    }
}
