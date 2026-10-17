package com.zap.exception.execution;

import com.zap.exception.ZapException;

public class CompilationFailedException extends ZapException {
    private final int exitCode;

    public CompilationFailedException(int exitCode) {
        super("Compilation failed (exit code " + exitCode + ").");
        this.exitCode = exitCode;
    }

    public int exitCode() {
        return exitCode;
    }
}
