package com.github.lemongrab32.service;

import com.github.lemongrab32.model.Article;
import com.github.lemongrab32.model.dto.ArticleRequest;
import com.github.lemongrab32.model.dto.ArticleResponse;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Map;

public interface ArticleService {

    /**
     * Возвращает все сохранённые статьи
     * @return список всех имеющихся в базе статей
     */
    List<ArticleResponse> getArticles();

    /**
     * Возвращает все сохранённые статьи c применением указанных фильтров
     * @return список всех имеющихся в базе статей, соответствующих критериям фильтрации
     */
    List<ArticleResponse> getArticles(Specification<Article> spec);

    /**
     * Ищет статью по её id
     * @param id идентификатор статьи для поиска в таблице
     * @return Искомую статью
     * @throws RuntimeException если не удалось найти статью
     */
    ArticleResponse getArticle(long id);

    /**
     * Создаёт новую запись в таблице с переданными данными
     * @param request данные новой статьи
     */
    void saveArticle(ArticleRequest request);

    /**
     * Ищет статью по её id и обновляет указанные данные
     * @param id идентификатор статьи для поиска в таблице
     * @param updates данные, которые необходимо изменить
     * @throws RuntimeException если не удалось найти статью
     */
    void updateArticle(long id, Map<String, String> updates);

    /**
     * Удаляет указанную статью
     * @param id идентификатор статьи для поиска в таблице
     * @throws RuntimeException если не удалось найти статью
     */
    void deleteArticle(long id);

}
