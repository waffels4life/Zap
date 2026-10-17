package com.zap.exception.config;

import com.zap.exception.ZapException;

public class ZapIoException extends ZapException {
    public ZapIoException(String message, Throwable cause) {
        super(message, cause);
    }
}
