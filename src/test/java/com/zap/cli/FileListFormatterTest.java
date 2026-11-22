package com.zap.cli;

import com.zap.project.ProjectFile;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FileListFormatterTest {

    private final FileListFormatter formatter = new FileListFormatter();

    @Test
    void shouldShowMessageWhenNoFilesAreFound() {
        assertEquals("No files found.", formatter.format(List.of()));
    }

    @Test
    void shouldFormatFileListAndAlignColumns() {
        List<ProjectFile> files = List.of(
                new ProjectFile(Path.of("assets", "logo.bin"), 1536),
                new ProjectFile(Path.of("src", "Main.java"), 1024)
        );
        String newline = System.lineSeparator();

        String expected = String.join(newline,
                "Files",
                "  PATH              SIZE",
                "  assets/logo.bin   1.5 KB",
                "  src/Main.java     1.0 KB",
                "",
                "2 files"
        );

        assertEquals(expected, formatter.format(files));
    }

    @Test
    void shouldFormatByteValuesWithoutDecimalPlaces() {
        List<ProjectFile> files = List.of(
                new ProjectFile(Path.of("empty.txt"), 0),
                new ProjectFile(Path.of("small.txt"), 1023)
        );
        String newline = System.lineSeparator();

        String expected = String.join(newline,
                "Files",
                "  PATH        SIZE",
                "  empty.txt   0 B",
                "  small.txt   1023 B",
                "",
                "2 files"
        );

        assertEquals(expected, formatter.format(files));
    }

    @Test
    void shouldFormatLargerSizesUsingBinaryUnits() {
        List<ProjectFile> files = List.of(
                new ProjectFile(Path.of("one-megabyte.bin"), 1024L * 1024),
                new ProjectFile(Path.of("one-gigabyte.bin"), 1024L * 1024 * 1024),
                new ProjectFile(Path.of("one-terabyte.bin"), 1024L * 1024 * 1024 * 1024)
        );

        String output = formatter.format(files);

        assertTrue(output.contains("1.0 MB"));
        assertTrue(output.contains("1.0 GB"));
        assertTrue(output.contains("1.0 TB"));
    }

    @Test
    void shouldUseSingularForOneFile() {
        String output = formatter.format(List.of(
                new ProjectFile(Path.of("Main.java"), 12)
        ));

        assertTrue(output.endsWith("1 file"));
    }
}
