package com.zap.exception.project;

import java.nio.file.Path;

public class AlreadyInitializedException extends RuntimeException {
    public AlreadyInitializedException(Path projectRoot) {
        super("Project is already initialized: " + projectRoot);
    }
}
