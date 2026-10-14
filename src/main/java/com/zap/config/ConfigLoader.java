package com.zap.config;

import com.zap.exception.ConfigIncorrectFormatException;
import com.zap.exception.ConfigIncorrectValuesException;
import com.zap.exception.ConfigMissingException;
import com.zap.exception.ConfigNotReadableException;
import com.zap.exception.ConfigurationException;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.MalformedInputException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.Properties;

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
