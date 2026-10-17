package com.zap.exception.execution;

import com.zap.exception.ZapException;

public class ExecutionInterruptedException extends ZapException {
    public ExecutionInterruptedException() {
        super("Execution interrupted.");
    }
}
