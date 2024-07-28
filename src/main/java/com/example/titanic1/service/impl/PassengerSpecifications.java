package com.example.titanic1.service.impl;

import com.example.titanic1.model.entity.PassengerEntity;
import com.example.titanic1.model.enums.Gender;
import org.springframework.data.jpa.domain.Specification;

public class PassengerSpecifications {

    public static Specification<PassengerEntity> nameContains(String name) {
        return (root, query, criteriaBuilder) -> {
            query.distinct(true); // Добавляем DISTINCT
            return criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + name.toLowerCase() + "%");
        };
    }

    public static Specification<PassengerEntity> isSurvived(Boolean survived) {
        return (root, query, criteriaBuilder) -> {
            query.distinct(true); // Добавляем DISTINCT
            return criteriaBuilder.equal(root.get("survived"), survived);
        };
    }

    public static Specification<PassengerEntity> isAdult(Boolean adult) {
        return (root, query, criteriaBuilder) -> {
            query.distinct(true); // Добавляем DISTINCT
            if (adult != null && adult) {
                return criteriaBuilder.greaterThan(root.get("age"), 18);
            } else {
                return criteriaBuilder.lessThanOrEqualTo(root.get("age"), 18);
            }
        };
    }

    public static Specification<PassengerEntity> hasGender(Gender gender) {
        return (root, query, criteriaBuilder) -> {
            query.distinct(true); // Добавляем DISTINCT
            return criteriaBuilder.equal(root.get("gender"), gender);
        };
    }

    public static Specification<PassengerEntity> hasNoRelatives(Boolean noRelatives) {
        return (root, query, criteriaBuilder) -> {
            query.distinct(true); // Добавляем DISTINCT
            return criteriaBuilder.and(
                    criteriaBuilder.equal(root.get("siblingsSpousesAboard"), 0),
                    criteriaBuilder.equal(root.get("parentsChildrenAboard"), 0)
            );
        };
    }
}