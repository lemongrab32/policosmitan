package com.github.lemongrab32.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;
import java.util.Set;

public record ArticleResponse(Long id,
                              String title,
                              @JsonProperty("short_description") String shortDescription,
                              String author, String content,
                              @JsonProperty("publishing_date") Date publishingDate,
                              Set<String> tags) {}
