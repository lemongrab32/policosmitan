package com.github.lemongrab32.service.impl;

import com.github.lemongrab32.model.Article;
import com.github.lemongrab32.model.dto.ArticleRequest;
import com.github.lemongrab32.model.dto.ArticleResponse;
import com.github.lemongrab32.repository.ArticleRepository;
import com.github.lemongrab32.service.ArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ArticleServiceImpl implements ArticleService {

    private final ArticleRepository articleRepository;

    @Override
    public List<ArticleResponse> getArticles() {
        return mapToResponses(articleRepository.findAll());
    }

    public List<ArticleResponse> getArticles(Specification<Article> spec) {
        return mapToResponses(articleRepository.findAll(spec));
    }

    @Override
    public ArticleResponse getArticle(long id) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Статья с id " + id + " не найдена"));
        return new ArticleResponse(
                article.getId(),
                article.getTitle(),
                article.getShortDescription(),
                article.getAuthor(),
                article.getContent()
        );
    }

    @Override
    public void saveArticle(ArticleRequest request) {
        Article article = Article.builder()
                .title(request.title())
                .shortDescription(request.shortDescription())
                .author(request.author())
                .content(request.content())
                .publishingDate(new Date())
                .build();

        articleRepository.save(article);
    }

    @Override
    public void updateArticle(long id, Map<String, String> updates) {
        Article article = articleRepository.findById(id)
                .orElseThrow(
                        () -> new RuntimeException("Статья с id " + id + " не найдена")
                );

        updates.forEach((key, value) -> {
            Field field = ReflectionUtils.findField(article.getClass(), key);

            if (field != null) {
                ReflectionUtils.makeAccessible(field);
                ReflectionUtils.setField(field, article, value);
            }
        });

        articleRepository.save(article);
    }

    @Override
    public void deleteArticle(long id) {
        articleRepository.deleteById(id);
    }

    private List<ArticleResponse> mapToResponses(List<Article> articles) {
        return articles.stream()
                .map(article -> new ArticleResponse(
                        article.getId(), article.getTitle(),
                        article.getShortDescription(),
                        article.getAuthor(), article.getContent()
                )).toList();
    }
}
