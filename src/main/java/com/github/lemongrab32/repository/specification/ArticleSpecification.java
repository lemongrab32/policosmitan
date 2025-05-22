package com.github.lemongrab32.repository.specification;

import com.github.lemongrab32.model.Article;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

public class ArticleSpecification implements Specification<Article> {

    private final SearchCriteria criteria;

    public ArticleSpecification(SearchCriteria criteria) {
        this.criteria = criteria;
    }

    @Override
    public Predicate toPredicate(Root<Article> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        if (criteria.operation().equalsIgnoreCase(":")) {
            return criteriaBuilder.like(
                    root.get(criteria.key()), "%" + criteria.value().toString() + "%"
            );
        }

        return null;
    }

}
