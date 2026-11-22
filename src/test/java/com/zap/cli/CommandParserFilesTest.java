package com.zap.cli;

import com.zap.exception.cli.CommandSyntaxException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CommandParserFilesTest {

    private final CommandParser parser = new CommandParser();

    @Test
    void shouldParseFilesCommand() {
        assertInstanceOf(Command.Files.class,
                parser.parse(new String[]{"files"}));
    }

    @Test
    void shouldRejectArgumentsForFilesCommand() {
        assertThrows(CommandSyntaxException.class,
                () -> parser.parse(new String[]{"files", "--sort", "size"}));
    }
}
