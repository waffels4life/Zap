package com.zap.config;

import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public record ZapConfig(Path sourceDirectory,
                        String compilerExecutable,
                        Path outputDirectory,
                        String runtimeExecutable,
                        String mainClass,
                        List<String> inputs,
                        List<String> args) {

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

        inputs = List.copyOf(inputs);
        args = List.copyOf(args);
    }

    public static ZapConfig defaults() {
        return new ZapConfig(
                Path.of("src/main/java"),
                "javac",
                Path.of(".zap/classes"),
                "java",
                ConfigKeys.AUTO_MAIN_CLASS,
                List.of(),
                List.of());
    }

    public ZapConfig withMainClass(String value) {
        return new ZapConfig(sourceDirectory, compilerExecutable, outputDirectory,
                runtimeExecutable, value, inputs, args);
    }

    public ZapConfig withInputs(List<String> value) {
        return new ZapConfig(sourceDirectory, compilerExecutable, outputDirectory,
                runtimeExecutable, mainClass, value, args);
    }

    public ZapConfig withArgs(List<String> value) {
        return new ZapConfig(sourceDirectory, compilerExecutable, outputDirectory,
                runtimeExecutable, mainClass, inputs, value);
    }

    public Map<String, String> asMap() {
        Map<String, String> map = new LinkedHashMap<>();
        map.put(ConfigKeys.SOURCE_DIRECTORY, portable(sourceDirectory));
        map.put(ConfigKeys.COMPILER_EXECUTABLE, compilerExecutable);
        map.put(ConfigKeys.OUTPUT_DIRECTORY, portable(outputDirectory));
        map.put(ConfigKeys.RUNTIME_EXECUTABLE, runtimeExecutable);
        map.put(ConfigKeys.MAIN_CLASS, mainClass);
        putIndexed(map, ConfigKeys.INPUT_PREFIX, inputs);
        putIndexed(map, ConfigKeys.ARGS_PREFIX, args);
        return map;
    }

    private static void putIndexed(Map<String, String> map,
                                   String prefix,
                                   List<String> values) {
        for (int i = 0; i < values.size(); i++) {
            map.put(prefix + i, values.get(i));
        }
    }

    private static String portable(Path path) {
        return path.toString().replace('\\', '/');
    }

    private static String requireNonBlank(String value, String key) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                    "Configuration value must not be blank: " + key);
        }
        return value;
    }
}