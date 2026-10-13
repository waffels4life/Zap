package com.zap.project;

import java.io.IOException;
import java.nio.charset.CharacterCodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 * Fully Qualified Class Name (FQCN)
 * The compiler works with file paths, but the java launcher needs the
 * full name of the main class (e.g. com.zap.app.Main) to run a program.
 *
 * This class finds Main.java and builds that name from its package declaration.
 */
public final class MainClassResolver {

    private static final String MAIN_CLASS = "Main";
    private static final String MAIN_FILE = MAIN_CLASS + ".java";

    /**
     * ^	      Start of the line (since the multiline option is enabled, each line counts)
     * \s*	      Any number of spaces, tabs, or empty lines (including zero)
     * package	  The exact word "package"
     * \s+	      At least one space
     * ( ... )	  Parentheses mean "capture this part"
     * [\w.]+	  One or more alphanumeric characters, underscores, or dots
     * \s*;	      Optional space followed by a semicolon
     */
    private static final Pattern PACKAGE_PATTERN = Pattern.compile(
            "^\\s*package\\s+([\\w.]+)\\s*;",
            Pattern.MULTILINE | Pattern.UNICODE_CHARACTER_CLASS);

    public String resolve(List<Path> sourceFiles) throws IOException {

        Objects.requireNonNull(sourceFiles, "sourceFiles");

        // find the main file
        Path mainFile = findMainFile(sourceFiles);

        // reading thr source code
        String source = Files.readString(mainFile);

        return extractPackage(source)
                .map(pkg -> pkg + "." + MAIN_CLASS)
                .orElse(MAIN_CLASS);
    }

    private static Path findMainFile(List<Path> sourceFiles) {

        Objects.requireNonNull(sourceFiles, "sourceFile");

        List<Path> candidates = sourceFiles.stream()
                .filter((path) -> path
                        .getFileName()
                        .toString()
                        .equals(MAIN_FILE))
                .toList();

        if (candidates.isEmpty())
            throw new IllegalStateException(
                    MAIN_FILE + " was not found. Create it in the source directory."
            );

        if (candidates.size() > 1)
            throw new IllegalStateException(
                    "Multiple " + MAIN_FILE + " files found: " + candidates
            );

        return candidates.getFirst();
    }

    private static String readSource(Path path) {
        try {
            return Objects.requireNonNull(Files.readString(path));
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // package-private on purpose, so it can be tested with plain strings
    static Optional<String> extractPackage(String source) {
        Matcher matcher = PACKAGE_PATTERN.matcher(source);
        return matcher.find()
                ? Optional.of(matcher.group(1))
                : Optional.empty();
    }
}
