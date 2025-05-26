package com.github.lemongrab32.model.dto;

import java.util.Set;

public record ArticleResponse(Long id, String title, String shortDescription, String author, String content, Set<String> tags) {}
