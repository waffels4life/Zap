package com.zap.cli.help;

import java.util.Objects;

public record OptionDoc(String syntax, String description) {

    public OptionDoc {
        Objects.requireNonNull(syntax, "syntax");
        Objects.requireNonNull(description, "description");
    }
}
