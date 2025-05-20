package com.github.lemongrab32.controller;

import com.github.lemongrab32.model.dto.ArticleRequest;
import com.github.lemongrab32.model.dto.ArticleResponse;
import com.github.lemongrab32.service.ArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Контроллер для обработки запросов на базовые CRUD операции со статьями
 */
@RestController
@RequestMapping("/api/v1/articles")
@RequiredArgsConstructor
public class MainController {

    private final ArticleService articleService;

    @GetMapping
    public ResponseEntity<?> getArticles() {
        return ResponseEntity.ok(articleService.getArticles());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getArticle(@PathVariable Long id) {
        ArticleResponse articleResponse;
        try {
            articleResponse = articleService.getArticle(id);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }

        return ResponseEntity.ok(articleResponse);
    }

    @PostMapping
    public ResponseEntity<?> createArticle(@RequestBody ArticleRequest request) {
        articleService.saveArticle(request);

        return ResponseEntity.ok("Статья успешно добавлена");
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> updateArticle(@PathVariable Long id, @RequestBody Map<String, String> updates) {
        try {
            articleService.updateArticle(id, updates);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }

        return ResponseEntity.ok("Данные выбранной статьи успешно изменены");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteArticle(@PathVariable Long id) {
        ArticleResponse article;

        try {
            article = articleService.getArticle(id);
            articleService.deleteArticle(id);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }

        return ResponseEntity.ok("Статья \"" + article.title() + "\" успешно удалена");
    }

}
