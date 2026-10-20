package com.zap.cli.help;

import java.util.List;
import java.util.Objects;

/*
 * zap run - Compile and run the project        <- name + summary
 *
 * Usage:                                       <- usages
 *   zap run [--input <value>...] ...
 *   zap -r
 *
 * Description:                                 <- description (one line per element)
 *   Compiles every .java file ...
 *
 * Options:                                     <- options (OptionDoc: syntax + text)
 *   --input <value>...   Lines sent to ...
 *
 * Examples:                                    <- examples
 *   zap run
 *
 * Aliases: -r                                  <- aliases
 */
public record CommandDoc(String name,
                         List<String> aliases,
                         String summary,
                         List<String> usages,
                         List<String> description,
                         List<OptionDoc> optionDocs,
                         List<String> examples) {
    public CommandDoc {

        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(summary, "summary");

        aliases     = List.copyOf(aliases);
        usages      = List.copyOf(usages);
        description = List.copyOf(description);
        optionDocs  = List.copyOf(optionDocs);
        examples    = List.copyOf(examples);
    }

    // true if the token is the name or one of the aliases
    public boolean matches(String token) {
        return name.equals(token) || aliases.contains(token);
    }

    // "run, -r" (name + aliases, used in the overview list)
    public String label() {
        return aliases.isEmpty()
                ? name
                : name + ", " + String.join(", ", aliases);
    }
}
