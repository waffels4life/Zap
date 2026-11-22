package com.zap.cli;

import com.zap.config.ConfigManager;
import com.zap.config.ZapConfig;
import com.zap.project.ProjectFileScanner;
import com.zap.project.ProjectLayout;
import com.zap.project.ProjectLocator;

import java.io.PrintStream;
import java.nio.file.Path;
import java.util.Objects;

public final class FilesCommandHandler {

    private final ProjectLocator locator;
    private final ConfigManager manager;
    private final ProjectFileScanner scanner;
    private final FileListFormatter formatter;
    private final PrintStream out;

    public FilesCommandHandler(ProjectLocator locator,
                               ConfigManager manager,
                               ProjectFileScanner scanner,
                               FileListFormatter formatter,
                               PrintStream out) {
        this.locator = Objects.requireNonNull(locator, "locator");
        this.manager = Objects.requireNonNull(manager, "manager");
        this.scanner = Objects.requireNonNull(scanner, "scanner");
        this.formatter = Objects.requireNonNull(formatter, "formatter");
        this.out = Objects.requireNonNull(out, "out");
    }

    public void handle() {
        Path root = locator.locate(locator.currentDirectory());
        ZapConfig config = manager.resolve(root);
        ProjectLayout layout = ProjectLayout.of(root, config);

        out.println(formatter.format(
                scanner.scan(layout.sourceDirectory())
        ));
    }
}
