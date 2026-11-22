package com.zap.project;

import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ProjectFileTest {

    @Test
    void shouldAcceptRelativePathAndNonNegativeSize() {
        ProjectFile file = new ProjectFile(Path.of("Main.java"), 128);

        assertEquals(Path.of("Main.java"), file.relativePath());
        assertEquals(128, file.sizeBytes());
    }

    @Test
    void shouldRejectNullPath() {
        assertThrows(NullPointerException.class,
                () -> new ProjectFile(null, 0));
    }

    @Test
    void shouldRejectAbsolutePath() {
        assertThrows(IllegalArgumentException.class,
                () -> new ProjectFile(Path.of(System.getProperty("user.home")), 0));
    }

    @Test
    void shouldRejectNegativeSize() {
        assertThrows(IllegalArgumentException.class,
                () -> new ProjectFile(Path.of("Main.java"), -1));
    }
}
