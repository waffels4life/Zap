package com.zap.config;

import com.zap.exception.config.ConfigKeyMissingException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

public final class ConfigValidator {

    private static final List<Exception> exceptionList = new ArrayList<>();
    private static final List<String> keys = List.of(
            ConfigKeys.SOURCE_DIRECTORY,
            ConfigKeys.COMPILER_EXECUTABLE,
            ConfigKeys.OUTPUT_DIRECTORY,
            ConfigKeys.RUNTIME_EXECUTABLE,
            ConfigKeys.MAIN_CLASS
    );

    public List<Exception> validate(Properties properties) throws Exception {

        Objects.requireNonNull(properties, "properties");
        if (properties.isEmpty())
            throw new ConfigKeyMissingException(
                    "Configuration is empty. at: " + properties.getClass()
            );
        /*
         * Checks whether if config file contains all the keys.
         */
        for (String key : keys)
            if (!properties.containsKey(key))
                exceptionList.add(
                        new ConfigKeyMissingException(
                                "Missing required configuration key: " + key
                        )
                );

        return exceptionList;
    }

    public int getExceptionListSize() {
        return exceptionList.size();
    }
}
