package com.zap.config;

public record ZapConfig(String sourceDirectory,
                        String compilerExecutable,
                        String outputDirectory,
                        String runtimeExecutable,
                        String mainClass) {

    public ZapConfig(
            String sourceDirectory,
            String compilerExecutable,
            String outputDirectory,
            String runtimeExecutable,
            String mainClass
    ) {
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
                mainClass, "main.class"
        );
    }

    public static ZapConfig defaults() {
        return new ZapConfig(
                "src/main/java",
                "javac",
                ".zap/classes",
                "java",
                "auto"
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
