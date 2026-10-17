package com.zap.config;

import java.util.List;

public sealed interface MainClassSearchResult {

    record Found(String className) implements MainClassSearchResult {}

    record NotFound() implements MainClassSearchResult {}

    record Ambiguous(List<String> candidates) implements MainClassSearchResult {

        public Ambiguous {
            candidates = List.copyOf(candidates);
        }
    }
}