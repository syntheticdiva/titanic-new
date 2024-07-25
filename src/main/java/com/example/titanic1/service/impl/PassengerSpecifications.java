package com.example.titanic1.service.impl;

import com.example.titanic1.model.entity.PassengerEntity;
import org.springframework.data.jpa.domain.Specification;

public class PassengerSpecifications {
    public static Specification<PassengerEntity> nameContains(String name) {
        return (root, query, cb) -> cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
    }

    public static Specification<PassengerEntity> isSurvived(boolean survived) {
        return (root, query, cb) -> cb.equal(root.get("survived"), survived);
    }

    public static Specification<PassengerEntity> isAdult(boolean adult) {
        return (root, query, cb) -> {
            if (adult) {
                return cb.greaterThan(root.get("age"), 16);
            } else {
                return cb.lessThanOrEqualTo(root.get("age"), 16);
            }
        };
    }

    public static Specification<PassengerEntity> hasGender(String gender) {
        return (root, query, cb) -> cb.equal(cb.lower(root.get("gender")), gender.toLowerCase());
    }

    public static Specification<PassengerEntity> hasNoRelatives(boolean noRelatives) {
        return (root, query, cb) -> {
            if (noRelatives) {
                return cb.equal(cb.sum(root.get("siblingsSpousesAboard"), root.get("parentsChildrenAboard")), 0);
            } else {
                return cb.notEqual(cb.sum(root.get("siblingsSpousesAboard"), root.get("parentsChildrenAboard")), 0);
            }
        };
    }
}
