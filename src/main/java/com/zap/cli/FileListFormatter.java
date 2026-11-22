package com.zap.cli;

import com.zap.project.ProjectFile;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

public final class FileListFormatter {

    private static final String INDENT = "  ";

    public String format(List<ProjectFile> files) {
        Objects.requireNonNull(files, "files");

        if (files.isEmpty())
            return "No files found.";

        int pathWidth = files.stream()
                .mapToInt(file -> portable(file.relativePath().toString()).length())
                .max()
                .orElse(0);

        StringBuilder result = new StringBuilder();
        result.append("Files")
                .append(System.lineSeparator())
                .append(INDENT)
                .append(pad("PATH", pathWidth))
                .append("   SIZE");

        for (ProjectFile file : files) {
            String path = portable(file.relativePath().toString());
            result.append(System.lineSeparator())
                    .append(INDENT)
                    .append(pad(path, pathWidth))
                    .append("   ")
                    .append(humanReadableSize(file.sizeBytes()));
        }

        result.append(System.lineSeparator())
                .append(System.lineSeparator())
                .append(files.size())
                .append(files.size() == 1 ? " file" : " files");

        return result.toString();
    }

    private static String humanReadableSize(long bytes) {
        if (bytes < 1024)
            return bytes + " B";

        String[] units = {"KB", "MB", "GB", "TB"};
        double size = bytes;
        int unit = -1;

        do {
            size /= 1024;
            unit++;
        } while (size >= 1024 && unit < units.length - 1);

        return String.format(Locale.ROOT, "%.1f %s", size, units[unit]);
    }

    private static String portable(String path) {
        return path.replace('\\', '/');
    }

    private static String pad(String value, int width) {
        return value + " ".repeat(Math.max(0, width - value.length()));
    }
}
