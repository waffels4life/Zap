package com.zap.config;

import com.zap.exception.ZapIoException;
import com.zap.exception.config.ConfigFileAlreadyExistsException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class ConfigWriter {

    /**
     * Creates the file. Throws ConfigFileAlreadyExistsException if it exists.
     */
    public void createNew(Path file, ZapConfig config) {
        write(file, config, false);
    }

    /**
     * Replaces the file atomically.
     */
    public void replace(Path file, ZapConfig config) {
        write(file, config, true);
    }

    private void write(Path file, ZapConfig config, boolean replace) {
        Objects.requireNonNull(file, "file");
        Objects.requireNonNull(config, "config");

        Path absolute = file.toAbsolutePath();
        Path temp = null;

        try {
            Files.createDirectories(absolute.getParent());
            temp = Files.createTempFile(absolute.getParent(), "config-", ".tmp");
            Files.write(temp, render(config), StandardCharsets.UTF_8);

            try {
                move(temp, absolute, replace);
            }
            catch (FileAlreadyExistsException e) {
                throw new ConfigFileAlreadyExistsException(absolute);
            }
        }
        catch (IOException e) {
            throw new ZapIoException("Cannot write config file: " + absolute, e);
        }
        finally {
            deleteQuietly(temp);
        }
    }

    private static void move(Path temp, Path target, boolean replace) throws IOException {
        if (!replace) {
            Files.move(temp, target);
            return;
        }

        try {
            Files.move(temp, target,
                    StandardCopyOption.ATOMIC_MOVE,
                    StandardCopyOption.REPLACE_EXISTING);
        }
        catch (AtomicMoveNotSupportedException e) {
            Files.move(temp, target, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    private static List<String> render(ZapConfig config) {
        List<String> lines = new ArrayList<>();
        lines.add("# Zap configuration");
        config.asMap().forEach((key, value) -> lines.add(key + "=" + escape(value)));
        return lines;
    }

    /*
     * Properties format: backslash and line breaks must be escaped,
     * and a leading space would be lost when reading.
     */
    private static String escape(String value) {
        StringBuilder result = new StringBuilder(value.length());

        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            switch (c) {
                case '\\' -> result.append("\\\\");
                case '\n' -> result.append("\\n");
                case '\r' -> result.append("\\r");
                case '\t' -> result.append("\\t");
                case '\f' -> result.append("\\f");
                case ' ' -> result.append(i == 0 ? "\\ " : " ");
                default -> result.append(c);
            }
        }
        return result.toString();
    }

    private static void deleteQuietly(Path file) {
        if (file == null) return;
        try {
            Files.deleteIfExists(file);
        }
        catch (IOException ignored) {
            // A leftover temp file is harmless and must not hide the real result.
        }
    }
}