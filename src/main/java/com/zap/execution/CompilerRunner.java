package com.zap.execution;

import com.zap.exception.ZapIoException;
import com.zap.exception.execution.CompilationFailedException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

public final class CompilerRunner {

    private final ProcessRunner processRunner;

    public CompilerRunner(ProcessRunner processRunner) {
        this.processRunner = Objects.requireNonNull(processRunner, "processRunner");
    }

    public void compile(String compiler,
                        List<Path> sources,
                        Path outputDirectory,
                        Path workingDirectory) {

        Objects.requireNonNull(outputDirectory, "outputDirectory");
        Path argumentFile = writeArgumentFile(sources);

        try {
            List<String> command = List.of(
                    compiler,
                    "-encoding", "UTF-8",
                    "-d", outputDirectory.toString(),
                    "@" + argumentFile
            );

            int exitCode = processRunner.run(
                    command, workingDirectory, List.of()
            );
            if (exitCode != 0)
                throw new CompilationFailedException(
                        exitCode
                );
        }
        finally {
            deleteQuietly(argumentFile);
        }
    }

    private static Path writeArgumentFile(List<Path> sources) {
        Objects.requireNonNull(sources, "sources");
        try {
            Path file = Files.createTempFile("zap-sources-", ".txt");
            List<String> lines = sources.stream()
                    .map(path -> "\"" + path.toString().replace('\\', '/') + "\"")
                    .toList();
            Files.write(file, lines, StandardCharsets.UTF_8);
            return file;
        }
        catch (IOException e) {
            throw new ZapIoException(
                    "Cannot prepare the source list for the compiler",
                    e
            );
        }
    }

    private static void deleteQuietly(Path file) {
        try {
            Files.deleteIfExists(file);
        } // A leftover temp file is harmless and must not hide the real result.
        catch (IOException _) {}
    }
}