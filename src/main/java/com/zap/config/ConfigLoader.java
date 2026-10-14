package com.zap.config;

import java.nio.file.Path;
import java.util.Objects;

public final class ConfigLoader {

    /*
     * These are the default paths
     * final path will always be at the end of project root
     * \..\..\..\final\ >> first .zap, then files
     */
    private static final String CONFIG_DIRECTORY = ".zap";
    private static final String CONFIG_FILE = "config.properties";

    /**
     * first we just check whether the entry path is empty or not >> no null & blank
     * Then it just resolve the path together.
     * @return projectRoot
     */
    public Path getConfigPath(Path projectRoot) {
        Objects.requireNonNull(projectRoot, "projectRoot");
        return projectRoot
                .resolve(CONFIG_DIRECTORY)
                .resolve(CONFIG_FILE);
    }


}
