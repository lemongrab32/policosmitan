package com.github.lemongrab32.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ArticleRequest(
        String title,
        @JsonProperty("short_description") String shortDescription,
        String author,
        String content
) {}
