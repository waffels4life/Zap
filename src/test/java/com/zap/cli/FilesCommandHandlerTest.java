package com.zap.cli;

import com.zap.config.ConfigLoader;
import com.zap.config.ConfigManager;
import com.zap.config.ConfigValidator;
import com.zap.config.ConfigWriter;
import com.zap.config.ZapConfig;
import com.zap.exception.project.ProjectNotInitializedException;
import com.zap.exception.project.ProjectStructureException;
import com.zap.project.ProjectFileScanner;
import com.zap.project.ProjectLocator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FilesCommandHandlerTest {

    @TempDir
    Path tempDirectory;

    private final String originalUserDir = System.getProperty("user.dir");

    @AfterEach
    void restoreWorkingDirectory() {
        System.setProperty("user.dir", originalUserDir);
    }

    @Test
    void shouldListFilesFromConfiguredSourceDirectory() throws Exception {
        Path project = Files.createDirectory(tempDirectory.resolve("project"));
        Path workingDirectory = Files.createDirectories(project.resolve("module/src"));
        Path source = Files.createDirectories(project.resolve("custom-sources"));
        Files.writeString(source.resolve("Main.java"), "class Main {}", StandardCharsets.UTF_8);
        Files.writeString(source.resolve("notes.txt"), "notes", StandardCharsets.UTF_8);
        writeConfig(project, Path.of("custom-sources"));
        System.setProperty("user.dir", workingDirectory.toString());

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (PrintStream out = new PrintStream(bytes, true, StandardCharsets.UTF_8)) {
            createHandler(out).handle();
        }

        String output = bytes.toString(StandardCharsets.UTF_8);
        assertTrue(output.contains("Main.java"));
        assertTrue(output.contains("notes.txt"));
        assertTrue(output.contains("2 files"));
    }

    @Test
    void shouldFailWhenProjectIsNotInitialized() throws Exception {
        Path workingDirectory = Files.createDirectory(
                tempDirectory.resolve("not-a-project"));
        assertThrows(ProjectNotInitializedException.class, () -> {
            System.setProperty("user.dir", workingDirectory.toString());
            createHandler(new PrintStream(new ByteArrayOutputStream())).handle();
        });
    }

    @Test
    void shouldFailWhenConfiguredSourceDirectoryDoesNotExist() throws Exception {
        Path project = Files.createDirectory(tempDirectory.resolve("project"));
        writeConfig(project, Path.of("missing-sources"));
        System.setProperty("user.dir", project.toString());

        assertThrows(ProjectStructureException.class,
                () -> createHandler(new PrintStream(new ByteArrayOutputStream())).handle());
    }

    private void writeConfig(Path project, Path sourceDirectory) {
        ZapConfig defaults = ZapConfig.defaults();
        ZapConfig config = new ZapConfig(
                sourceDirectory,
                defaults.compilerExecutable(),
                defaults.outputDirectory(),
                defaults.runtimeExecutable(),
                defaults.mainClass(),
                defaults.inputs(),
                defaults.args(),
                defaults.classpath());
        new ConfigWriter().createNew(
                project.resolve(".zap/config.properties"), config);
    }

    private FilesCommandHandler createHandler(PrintStream out) {
        ConfigManager manager = new ConfigManager(
                new ConfigLoader(), new ConfigValidator(), new ConfigWriter());
        return new FilesCommandHandler(
                new ProjectLocator(), manager, new ProjectFileScanner(),
                new FileListFormatter(), out);
    }
}
