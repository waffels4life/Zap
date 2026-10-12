package com.zap.project;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class MainClassResolver {

    private static final Pattern PACKAGE_PATTERN =
            Pattern.compile(
                    "^\\s*package\\s+([\\w.]+)\\s*;",
                    Pattern.MULTILINE
            );

    public String resolve(List<Path> sourceFiles) throws IOException {

        Path mainFile = sourceFiles.stream()
                .filter(path -> path.getFileName()
                        .toString()
                        .equals("Main.java"))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        "Main.java was not found."
                ));

        String source = Files.readString(mainFile);
        Matcher matcher = PACKAGE_PATTERN.matcher(source);

        if (matcher.find()) return matcher.group(1) + ".Main";

        return "Main";
    }
}
