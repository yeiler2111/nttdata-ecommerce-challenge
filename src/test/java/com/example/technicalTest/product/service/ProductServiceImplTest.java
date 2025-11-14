package com.example.technicalTest.product.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import com.example.technicalTest.product.dto.ProductRequest;
import com.example.technicalTest.product.dto.ProductResponse;
import com.example.technicalTest.product.entity.Product;
import com.example.technicalTest.product.repository.ProductRepository;

class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSearchProducts() {
        Pageable pageable = PageRequest.of(0, 10);
        Product p = new Product();
        p.setId(1L);
        p.setName("Laptop");
        p.setPrice(BigDecimal.valueOf(1000));

        when(productRepository.findAll(
                Mockito.<Specification<Product>>any(),
                eq(pageable))).thenReturn(new PageImpl<>(List.of(p)));

        Page<ProductResponse> result = productService.search(
                "lap", null, null, pageable);

        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).name()).isEqualTo("Laptop");
    }

    @Test
    void testCreateProduct() {
        ProductRequest req = new ProductRequest("Keyboard", "RGB", BigDecimal.TEN, 5);

        Product saved = new Product();
        saved.setId(10L);
        saved.setName("Keyboard");

        when(productRepository.save(any())).thenReturn(saved);

        ProductResponse res = productService.create(req);

        assertThat(res.id()).isEqualTo(10L);
        assertThat(res.name()).isEqualTo("Keyboard");
    }

    @Test
    void testUpdateProduct() {
        Product existing = new Product();
        existing.setId(5L);
        existing.setName("Old");

        when(productRepository.findById(5L)).thenReturn(Optional.of(existing));
        when(productRepository.save(any())).thenReturn(existing);

        ProductRequest req = new ProductRequest("New", "OK", BigDecimal.ONE, 3);

        ProductResponse res = productService.update(5L, req);

        assertThat(res.name()).isEqualTo("New");
    }
}
