package com.github.lemongrab32.repository.specification;

import com.github.lemongrab32.model.Article;
import io.micrometer.common.util.StringUtils;
import jakarta.persistence.criteria.*;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Slf4j
@Getter
public class ArticleSpecification implements Specification<Article> {

    private SearchCriteria criteria;

    public ArticleSpecification() {
    }

    public ArticleSpecification(SearchCriteria criteria) {
        checkCriteria(criteria);

        this.criteria = criteria;
    }

    @Override
    public Predicate toPredicate(Root<Article> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        var operation = this.criteria.operation();
        Path<Object> expression = root.get(this.criteria.key());
        String value = criteria.value().toString();
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
        Date date = new Date();

        try {
            date = formatter.parse(value);
        } catch (ParseException e) {
            log.error("Не удалось распознать корректную дату в запросе");
        }

        switch (operation) {
            case EQ -> {
                if ("tags".equals(this.criteria.key())) {
                    String[] tags = value.split(",");
                    Predicate predicate = criteriaBuilder.isMember(tags[0], root.get("tags"));
                    for (int i = 1; i < tags.length; i++) {
                        predicate = criteriaBuilder.or(predicate, criteriaBuilder.isMember(tags[i], root.get("tags")));
                    }
                    return predicate;
                }
                return criteriaBuilder.equal(expression, value);
            }
            case LIKE -> {
                String likeString = "%" + value + "%";
                return criteriaBuilder.like(expression.as(String.class), likeString);
            }
            case GT -> {
                if ("publishingDate".equals(this.criteria.key())) {
                    return criteriaBuilder.greaterThan(root.get("publishingDate"), date);
                }
            }
            case GE -> {
                if ("publishingDate".equals(this.criteria.key())) {
                    return criteriaBuilder.greaterThanOrEqualTo(root.get("publishingDate"), date);
                }
            }
            case LT -> {
                if ("publishingDate".equals(this.criteria.key())) {
                    return criteriaBuilder.lessThan(root.get("publishingDate"), date);
                }
            }
            case LE -> {
                if ("publishingDate".equals(this.criteria.key())) {
                    return criteriaBuilder.lessThanOrEqualTo(root.get("publishingDate"), date);
                }
            }

        }

        return null;
    }

    private void checkCriteria(SearchCriteria criteria) {
        if (criteria == null) {
            throw new IllegalArgumentException("SearchCriteria cannot be null");
        }

        if (StringUtils.isBlank(criteria.key())) {
            throw new IllegalArgumentException("Field must be not null");
        }
        if (criteria.operation() == null) {
            throw new IllegalArgumentException("Operation must be not null");
        }
        if (criteria.value() == null) {
            throw new IllegalArgumentException("Value must be not null");
        }
    }
}
