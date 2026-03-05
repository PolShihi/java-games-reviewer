package com.project.gamereviewer.specification;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.project.gamereviewer.dto.filter.GameFilterDto;
import com.project.gamereviewer.entity.Game;
import com.project.gamereviewer.entity.Genre;
import com.project.gamereviewer.entity.Review;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;

public class GameSpecification {

    private GameSpecification() {
    }

    public static Specification<Game> withFilters(GameFilterDto filter) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filter.title() != null && !filter.title().isBlank()) {
                predicates.add(
                    criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("title")),
                        "%" + filter.title().toLowerCase() + "%"
                    )
                );
            }

            if (filter.yearFrom() != null) {
                predicates.add(
                    criteriaBuilder.greaterThanOrEqualTo(root.get("releaseYear"), filter.yearFrom())
                );
            }

            if (filter.yearTo() != null) {
                predicates.add(
                    criteriaBuilder.lessThanOrEqualTo(root.get("releaseYear"), filter.yearTo())
                );
            }

            if (filter.genreIds() != null && !filter.genreIds().isEmpty()) {
                Join<Game, Genre> genreJoin = root.join("genres", JoinType.INNER);
                predicates.add(genreJoin.get("id").in(filter.genreIds()));
                
                query.groupBy(root.get("id"));
                query.having(
                    criteriaBuilder.equal(
                        criteriaBuilder.countDistinct(genreJoin.get("id")),
                        (long) filter.genreIds().size()
                    )
                );
            }

            if (filter.developerId() != null) {
                predicates.add(
                    criteriaBuilder.equal(root.get("developer").get("id"), filter.developerId())
                );
            }

            if (filter.publisherId() != null) {
                predicates.add(
                    criteriaBuilder.equal(root.get("publisher").get("id"), filter.publisherId())
                );
            }

            if (filter.ratingFrom() != null || filter.ratingTo() != null) {
                Subquery<Double> ratingSubquery = query.subquery(Double.class);
                Root<Review> reviewRoot = ratingSubquery.from(Review.class);
                ratingSubquery.select(criteriaBuilder.avg(reviewRoot.get("score")));
                ratingSubquery.where(criteriaBuilder.equal(reviewRoot.get("game").get("id"), root.get("id")));

                if (filter.ratingFrom() != null) {
                    predicates.add(
                        criteriaBuilder.greaterThanOrEqualTo(ratingSubquery, filter.ratingFrom())
                    );
                }

                if (filter.ratingTo() != null) {
                    predicates.add(
                        criteriaBuilder.lessThanOrEqualTo(ratingSubquery, filter.ratingTo())
                    );
                }
            }

            query.distinct(true);

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
