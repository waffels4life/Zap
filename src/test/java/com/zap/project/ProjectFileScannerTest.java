package com.zap.project;

import com.zap.exception.project.ProjectStructureException;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ProjectFileScannerTest {

    @TempDir
    Path tempDirectory;

    private final ProjectFileScanner scanner = new ProjectFileScanner();

    @Test
    void shouldFindAllRegularFilesRecursivelyAndSortByRelativePath() throws IOException {
        Path source = Files.createDirectory(tempDirectory.resolve("src"));
        Path nested = Files.createDirectories(source.resolve("com/example"));
        Path assets = Files.createDirectories(source.resolve("assets"));

        Files.writeString(nested.resolve("Main.java"), "class Main {}");
        Files.writeString(source.resolve("README.md"), "Zap");
        Files.write(assets.resolve("logo.bin"), new byte[]{1, 2, 3, 4});

        List<ProjectFile> files = scanner.scan(source);

        assertEquals(List.of(
                new ProjectFile(Path.of("README.md"), 3),
                new ProjectFile(Path.of("assets", "logo.bin"), 4),
                new ProjectFile(Path.of("com", "example", "Main.java"), 13)
        ), files);
    }

    @Test
    void shouldReturnEmptyListWhenSourceDirectoryContainsNoFiles() throws IOException {
        Path source = Files.createDirectory(tempDirectory.resolve("src"));
        Files.createDirectories(source.resolve("empty/nested"));

        assertEquals(List.of(), scanner.scan(source));
    }

    @Test
    void shouldNotFollowSymbolicLinks() throws IOException {
        Path source = Files.createDirectory(tempDirectory.resolve("src"));
        Path target = Files.writeString(tempDirectory.resolve("outside.txt"), "outside");
        Path link = source.resolve("linked.txt");

        try {
            Files.createSymbolicLink(link, target);
        }
        catch (UnsupportedOperationException | SecurityException | IOException e) {
            Assumptions.assumeTrue(false,
                    "Symbolic links are not available in this environment: " + e);
        }

        assertEquals(List.of(), scanner.scan(source));
    }

    @Test
    void shouldNotFollowSymbolicLinksToDirectories() throws IOException {
        Path source = Files.createDirectory(tempDirectory.resolve("src"));
        Path outside = Files.createDirectory(tempDirectory.resolve("outside"));
        Files.writeString(outside.resolve("hidden.txt"), "outside");

        try {
            Files.createSymbolicLink(source.resolve("linked-directory"), outside);
        }
        catch (UnsupportedOperationException | SecurityException | IOException e) {
            Assumptions.assumeTrue(false,
                    "Symbolic links are not available in this environment: " + e);
        }

        assertEquals(List.of(), scanner.scan(source));
    }

    @Test
    void shouldRejectMissingSourceDirectory() {
        Path missing = tempDirectory.resolve("missing");

        assertThrows(ProjectStructureException.class,
                () -> scanner.scan(missing));
    }

    @Test
    void shouldRejectPathThatIsNotADirectory() throws IOException {
        Path file = Files.writeString(tempDirectory.resolve("source.txt"), "not a directory");

        assertThrows(ProjectStructureException.class,
                () -> scanner.scan(file));
    }
}
