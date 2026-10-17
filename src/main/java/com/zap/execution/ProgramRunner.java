package com.zap.execution;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class ProgramRunner {

    public record Launch(String runtimeExecutable,
                         Path classDirectory,
                         String mainClass,
                         List<String> args,
                         List<String> inputs,
                         Path workingDirectory) {

        public Launch {
            Objects.requireNonNull(runtimeExecutable, "runtimeExecutable");
            Objects.requireNonNull(classDirectory, "classDirectory");
            Objects.requireNonNull(mainClass, "mainClass");
            Objects.requireNonNull(workingDirectory, "workingDirectory");
            args = List.copyOf(args);
            inputs = List.copyOf(inputs);
        }
    }

    private final ProcessRunner processRunner;

    public ProgramRunner(ProcessRunner processRunner) {
        this.processRunner = Objects.requireNonNull(processRunner, "processRunner");
    }

    public int run(Launch launch) {
        List<String> command = new ArrayList<>();
        command.add(launch.runtimeExecutable());
        command.add("-cp");
        command.add(launch.classDirectory().toString());
        command.add(launch.mainClass());
        command.addAll(launch.args());

        return processRunner.run(command, launch.workingDirectory(), launch.inputs());
    }
}