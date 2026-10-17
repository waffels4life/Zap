package com.zap.exception;

public abstract class ZapException extends RuntimeException {

    protected ZapException(String message) {
        super(message);
    }

    protected ZapException(String message, Throwable cause) {
        super(message, cause);
    }
}
