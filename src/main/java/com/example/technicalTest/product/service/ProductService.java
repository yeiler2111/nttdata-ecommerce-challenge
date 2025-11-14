package com.example.technicalTest.product.service;

import java.math.BigDecimal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.technicalTest.product.dto.ProductRequest;
import com.example.technicalTest.product.dto.ProductResponse;

public interface ProductService {

    Page<ProductResponse> search(
            String name,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Pageable pageable
    );

    ProductResponse create(ProductRequest request);

    ProductResponse update(Long id, ProductRequest request);
}
