package com.zap.cli;

import java.util.List;

public sealed interface Command {

    record Init(String mainClass) implements Command {}

    record Run(List<String> inputs, List<String> args) implements Command {

        public Run {
            inputs = List.copyOf(inputs);
            args = List.copyOf(args);
        }
    }

    record Help() implements Command {}

    record HelpTopic(String command) implements Command {}

    record Version() implements Command {}

    sealed interface ConfigCommand extends Command {}

    record ConfigSetInput(List<String> values) implements ConfigCommand {

        public ConfigSetInput {
            values = List.copyOf(values);
        }
    }

    record ConfigResetInput() implements ConfigCommand {}

    record ConfigSetArgs(List<String> values) implements ConfigCommand {

        public ConfigSetArgs {
            values = List.copyOf(values);
        }
    }

    record ConfigResetArgs() implements ConfigCommand {}

    record ConfigSetMain(String mainClass) implements ConfigCommand {}

    record ConfigResetMain() implements ConfigCommand {}

    record ConfigShow() implements ConfigCommand {}

    record ConfigResetAll() implements ConfigCommand {}
}