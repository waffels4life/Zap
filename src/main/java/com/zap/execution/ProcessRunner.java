package com.zap.execution;

import com.zap.exception.ZapIoException;
import com.zap.exception.execution.ExecutionInterruptedException;

import java.io.IOException;
import java.io.Writer;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

public final class ProcessRunner {

    /**
     * Runs a process and returns its exit code.
     * Output and errors go straight to the terminal.
     * If stdinLines is empty, the terminal stdin is inherited;
     * otherwise the lines are fed in order and then the input is closed.
     */
    public int run(List<String> command, Path workingDirectory, List<String> stdinLines) {
        Objects.requireNonNull(command, "command");
        Objects.requireNonNull(workingDirectory, "workingDirectory");
        Objects.requireNonNull(stdinLines, "stdinLines");

        boolean piped = !stdinLines.isEmpty();

        ProcessBuilder builder = new ProcessBuilder(command)
                .directory(workingDirectory.toFile())
                .redirectOutput(ProcessBuilder.Redirect.INHERIT)
                .redirectError(ProcessBuilder.Redirect.INHERIT)
                .redirectInput(piped
                        ? ProcessBuilder.Redirect.PIPE
                        : ProcessBuilder.Redirect.INHERIT);

        Process process;
        try {
            process = builder.start();
        }
        catch (IOException e) {
            throw new ZapIoException("Cannot start '" + command.getFirst()
                    + "'. Is it installed and on PATH?", e);
        }

        if (piped) {
            feed(process, stdinLines);
        }

        try {
            return process.waitFor();
        }
        catch (InterruptedException e) {
            process.destroy();
            Thread.currentThread().interrupt();
            throw new ExecutionInterruptedException();
        }
    }

    private static void feed(Process process, List<String> lines) {
        Thread.ofVirtual().start(() -> {
            try (Writer writer = new OutputStreamWriter(
                    process.getOutputStream(), StandardCharsets.UTF_8)) {

                for (String line : lines) {
                    writer.write(line);
                    writer.write('\n');
                }
            }
            catch (IOException _) {
                // The program ended before reading all inputs. That is not an error.
            }
        });
    }
}