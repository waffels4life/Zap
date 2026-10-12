package com.zap.execution;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public final class CompilerRunner {

    public int compile(List<Path> sourceFiles, Path outputDirectory)
            throws IOException, InterruptedException {

        if (sourceFiles.isEmpty())
            throw new IllegalArgumentException(
                    "No Java source files found."
            );

        Files.createDirectories(outputDirectory);

        List<String> command = new ArrayList<>();
        command.add("javac");
        command.add("-d");
        command.add(outputDirectory.toAbsolutePath().toString());

        for (Path sourceFile : sourceFiles)
            command.add(sourceFile.toAbsolutePath().toString());

        Process process = new ProcessBuilder(command)
                .inheritIO()
                .start();

        return process.waitFor();
    }
}
