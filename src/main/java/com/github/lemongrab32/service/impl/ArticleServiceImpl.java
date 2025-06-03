package com.github.lemongrab32.service.impl;

import com.github.lemongrab32.model.Article;
import com.github.lemongrab32.model.dto.ArticleRequest;
import com.github.lemongrab32.model.dto.ArticleResponse;
import com.github.lemongrab32.repository.ArticleRepository;
import com.github.lemongrab32.repository.specification.ArticleSpecification;
import com.github.lemongrab32.repository.specification.Operation;
import com.github.lemongrab32.repository.specification.SearchCriteria;
import com.github.lemongrab32.service.ArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class ArticleServiceImpl implements ArticleService {

    private final ArticleRepository articleRepository;

    @Override
    public List<ArticleResponse> getArticles() {
        return mapToResponses(articleRepository.findAll());
    }

    @Override
    public List<ArticleResponse> getArticles(String search) {
        String[] conditions = search.split(";"); // Критерии фильтрации разделяются знаком ";"
        ArticleSpecification specification = new ArticleSpecification();
        int count = 0; // Счётчик для прохода по критериям

        /*
            Формат ввода критериев поиска:
            [название поля]<оператор сравнения>[искомое значение]
         */
        Pattern regex = Pattern.compile("([A-Za-z]+)(:|<|<=|>|>=|=)(.+)");
        Matcher matcher = regex.matcher(conditions[count++]); // Матчер для первого критерия

        // Если совпадение найдено, то добавляем в спецификацию критерий
        if (matcher.find()) {
            specification = (ArticleSpecification) Specification.where(
                    new ArticleSpecification(
                            mapToCriteria(matcher.group(1), matcher.group(2), matcher.group(3))
                    )
            );

            // Обновляем значение матчера на следующий критерий, если не достигли конца массива,
            // иначе ищем данные со сформированной спецификацией
            if (count == conditions.length) {
                return mapToResponses(articleRepository.findAll(specification));
            } else {
                matcher = regex.matcher(conditions[count++]);
            }
        }

        // Обработка остальных критериев
        while (matcher.find()) {
            if (count == conditions.length) {
                return mapToResponses(articleRepository.findAll(specification));
            }

            specification.and(new ArticleSpecification(
                    mapToCriteria(matcher.group(1), matcher.group(2), matcher.group(3))
            ));
            matcher = regex.matcher(conditions[count++]);
        }

        if (specification.getCriteria() == null) {
            return List.of();
        }

        return mapToResponses(articleRepository.findAll(specification));
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
                article.getContent(),
                article.getPublishingDate(),
                article.getTags()
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
                .tags(request.tags())
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
            Field field = ReflectionUtils.findField(article.getClass(), key); // Получаем поле, значение которого нужно обновить

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

    /**
     * Преобразует объекты из базы в DTO
     * @param articles найденные в базе объекты статей
     * @return Список преобразованных в DTO статей
     */
    private List<ArticleResponse> mapToResponses(List<Article> articles) {
        return articles.stream()
                .map(article -> new ArticleResponse(
                        article.getId(), article.getTitle(),
                        article.getShortDescription(),
                        article.getAuthor(), article.getContent(),
                        article.getPublishingDate(),
                        article.getTags()
                )).toList();
    }

    /**
     * Преобразует значение операторов критерия поиска к соответствующим значениям enum Operation
     * @param field поле, по которому осуществляется фильтрация
     * @param operation оператор сравнения
     * @param value искомое значение
     * @return Сформированный объект критерия поиска
     */
    private SearchCriteria mapToCriteria(String field, String operation, String value) {
        switch (operation) {
            case ":" -> {
                return new SearchCriteria(field, Operation.LIKE, value);
            }
            case "<" -> {
                return new SearchCriteria(field, Operation.LT, value);
            }
            case "<=" -> {
                return new SearchCriteria(field, Operation.LE, value);
            }
            case ">" -> {
                return new SearchCriteria(field, Operation.GT, value);
            }
            case ">=" -> {
                return new SearchCriteria(field, Operation.GE, value);
            }
            case "=" -> {
                return new SearchCriteria(field, Operation.EQ, value);
            }
        }

        return null;
    }
}
