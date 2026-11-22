package com.zap.cli.help;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class FilesHelpTest {

    @Test
    void shouldDocumentFilesCommandBehavior() {
        CommandDoc files = HelpCatalog.standard().require("files");

        assertTrue(files.usages().contains("zap files"));
        assertTrue(String.join(" ", files.description())
                .contains("every regular file"));
        assertTrue(String.join(" ", files.description())
                .contains("Symbolic links are not followed"));
        assertTrue(files.optionDocs().isEmpty());
    }

    @Test
    void shouldIncludeFilesCommandInHelpOverview() {
        String overview = new HelpFormatter().overview(HelpCatalog.standard().all());
        String detail = new HelpFormatter().detail(
                HelpCatalog.standard().require("files"));

        assertTrue(overview.contains("files"));
        assertTrue(detail.contains("zap files"));
        assertTrue(detail.contains("Symbolic links are not followed"));
    }
}
