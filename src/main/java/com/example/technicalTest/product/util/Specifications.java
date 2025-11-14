package com.example.technicalTest.product.util;

import org.springframework.data.jpa.domain.Specification;

public class Specifications {

    public static <T> Specification<T> empty() {
        return (root, query, cb) -> cb.conjunction();
    }
}
