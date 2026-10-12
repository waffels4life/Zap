package com.zap.project;

import java.nio.file.Files;
import java.nio.file.Path;

public final class ProjectLocator {

    private static final String CONFIG_FILE = "zap.properties";

    public Path locate() {
        /*
         * while running, Java will prepare some system attributes; like Java version,
         * SystemOS, etc. one of them is [user.dir]
         *
         * user.dir >> folder where the program was started via terminal
         *
         * [NOTE]   >> user.dir is NOT the folder that contains classes and files.
         *          >> It's just the folder that user stood and used the terminal.
         *
         * cd /home/arsam/phantom
         * zap run => user.dir : /home/arsam/phantom
         */
        Path workingDirectory = Path.of(System.getProperty("user.dir"))
                .toAbsolutePath()
                .normalize();

        if (!Files.isDirectory(workingDirectory))
            throw new IllegalStateException(
                    "Working directory does not exist: "
                            + workingDirectory
            );

        return workingDirectory;
    }
}
