package com.zap.execution;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public final class ProgramRunner {
    public int run(Path classDirectory, String mainClass)
            throws IOException, InterruptedException {

        List<String> command = new ArrayList<>();
        command.add("java");
        command.add("-cp");
        command.add(classDirectory.toAbsolutePath().toString());
        command.add(mainClass);

        Process process = new ProcessBuilder(command)
                .inheritIO()
                .start();

        return process.waitFor();
    }
}
