package com.zap.project.mainclass;

import com.zap.config.ConfigKeys;
import com.zap.exception.config.MainClassException;

import java.nio.file.Path;
import java.util.Objects;

public final class MainClassResolver {

    private final MainClassFinder finder;

    public MainClassResolver(MainClassFinder finder) {
        this.finder = Objects.requireNonNull(finder, "finder");
    }

    public String resolve(String configuredMainClass, Path sourceDirectory) {
        Objects.requireNonNull(configuredMainClass, "configuredMainClass");
        Objects.requireNonNull(sourceDirectory, "sourceDirectory");

        if (!configuredMainClass.equals(ConfigKeys.AUTO_MAIN_CLASS)) {
            return configuredMainClass;
        }

        return switch (finder.find(sourceDirectory)) {
            case MainClassSearchResult.Found found -> found.className();
            case MainClassSearchResult.NotFound notFound ->
                    throw MainClassException.notFound(sourceDirectory);
            case MainClassSearchResult.Ambiguous ambiguous ->
                    throw MainClassException.ambiguous(ambiguous.candidates());
        };
    }
}