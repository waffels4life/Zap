package com.zap.exception.config;

import com.zap.exception.ZapException;

import java.nio.file.Path;

public class ProjectNotInitializedException extends ZapException {
    public ProjectNotInitializedException(Path start) {
        super("Project is not initialized: " + start
                + ". Run `zap init` first.");
    }
}
