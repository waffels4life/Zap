package com.zap.execution;

import java.util.List;
import java.util.Objects;

public record RunOptions(List<String> inputs, List<String> args) {

    public RunOptions {
        Objects.requireNonNull(inputs, "inputs");
        Objects.requireNonNull(args, "args");
        inputs = List.copyOf(inputs);
        args = List.copyOf(args);
    }

    public static RunOptions none() {
        return new RunOptions(List.of(), List.of());
    }
}