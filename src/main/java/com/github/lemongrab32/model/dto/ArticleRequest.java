package com.github.lemongrab32.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Size;

import java.util.Set;

public record ArticleRequest(
        @Size(min = 3, max = 70, message = "Длина заголовка статьи должна находиться в промежутке от 3 до 70 символов")
        String title,
        @JsonProperty("short_description")
        @Size(max = 256, message = "Длина краткого описания статьи не должна превышать 256 символов")
        String shortDescription,
        @Size(min = 2, max = 30, message = "Длина имени автора должна находиться в промежутке от 2 до 30 символов")
        String author,
        String content,
        @Size(max = 30, message = "Максимальное количество тегов статьи - 30")
        Set<String> tags
) {}
