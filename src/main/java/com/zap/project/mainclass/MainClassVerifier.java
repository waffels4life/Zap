package com.zap.project.mainclass;

import com.zap.exception.ZapIoException;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.Objects;

public final class MainClassVerifier {

    public MainClassStatus verify(Path classesDirectory, String mainClass) {
        Objects.requireNonNull(classesDirectory, "classesDirectory");
        Objects.requireNonNull(mainClass, "mainClass");

        try (URLClassLoader loader = new URLClassLoader(
                new URL[]{classesDirectory.toUri().toURL()},
                ClassLoader.getPlatformClassLoader())) {

            Class<?> type = Class.forName(mainClass, false, loader);
            return inspect(type);
        }
        catch (ClassNotFoundException e) {
            return MainClassStatus.CLASS_NOT_FOUND;
        }
        catch (LinkageError e) {
            return MainClassStatus.CLASS_LOAD_FAILED;
        }
        catch (IOException e) {
            throw new ZapIoException(
                    "Cannot open classes directory: " + classesDirectory, e);
        }
    }

    private static MainClassStatus inspect(Class<?> type) {
        Method main;
        try {
            main = type.getMethod("main", String[].class);
        }
        catch (NoSuchMethodException e) {
            return MainClassStatus.MAIN_METHOD_MISSING;
        }

        if (!Modifier.isStatic(main.getModifiers())) {
            return MainClassStatus.MAIN_METHOD_NOT_STATIC;
        }
        if (main.getReturnType() != void.class) {
            return MainClassStatus.MAIN_METHOD_NOT_VOID;
        }
        return MainClassStatus.VALID;
    }
}