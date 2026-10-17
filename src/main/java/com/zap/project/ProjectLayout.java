package com.zap.project;

import com.zap.config.ConfigPaths;
import com.zap.config.ZapConfig;
import com.zap.exception.config.ProjectStructureException;

import java.nio.file.Path;
import java.util.Objects;

public record ProjectLayout(Path root, Path sourceDirectory, Path outputDirectory) {

    public ProjectLayout {
        Objects.requireNonNull(root, "root");
        Objects.requireNonNull(sourceDirectory, "sourceDirectory");
        Objects.requireNonNull(outputDirectory, "outputDirectory");
    }

    public static ProjectLayout of(Path projectRoot, ZapConfig config) {
        Path root = projectRoot.toAbsolutePath().normalize();
        Path source = root.resolve(config.sourceDirectory()).normalize();
        Path output = root.resolve(config.outputDirectory()).normalize();

        if (!output.startsWith(root) || output.equals(root)) {
            throw unsafe(output, "must be inside the project and not the project root");
        }
        if (output.startsWith(source) || source.startsWith(output)) {
            throw unsafe(output, "must not overlap the source directory");
        }
        if (ConfigPaths.configFile(root).startsWith(output)) {
            throw unsafe(output, "must not contain the Zap config file");
        }

        return new ProjectLayout(root, source, output);
    }

    private static ProjectStructureException unsafe(Path output, String reason) {
        return new ProjectStructureException(
                "Unsafe output directory " + output + ": " + reason);
    }
}