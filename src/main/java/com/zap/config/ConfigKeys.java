package com.zap.config;

/*
 * This class stores all the key values information.
 * this class cannot be extended.
 */
public final class ConfigKeys {

    // no instance creation allowed.
    private ConfigKeys() {}

    public static final String SOURCE_DIRECTORY = "source.directory";
    public static final String COMPILER_EXECUTABLE = "compiler.executable";
    public static final String OUTPUT_DIRECTORY = "output.directory";
    public static final String RUNTIME_EXECUTABLE = "runtime.executable";
    public static final String MAIN_CLASS = "main.class";
}
