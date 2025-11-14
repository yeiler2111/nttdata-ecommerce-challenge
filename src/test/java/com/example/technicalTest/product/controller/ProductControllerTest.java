package com.example.technicalTest.product.controller;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.example.technicalTest.product.dto.ProductRequest;
import com.example.technicalTest.product.dto.ProductResponse;
import com.example.technicalTest.product.service.ProductService;

class ProductControllerTest {

    @Mock
    private ProductService productService;

    @InjectMocks
    private ProductController productController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void list_ShouldReturnOkWithPageFromService() {

        String name = "Producto";
        BigDecimal minPrice = BigDecimal.valueOf(10);
        BigDecimal maxPrice = BigDecimal.valueOf(100);
        Pageable pageable = PageRequest.of(0, 10);

        ProductResponse productResponse = mock(ProductResponse.class);
        Page<ProductResponse> page = new PageImpl<>(List.of(productResponse));

        when(productService.search(name, minPrice, maxPrice, pageable))
                .thenReturn(page);

        ResponseEntity<Page<ProductResponse>> responseEntity = productController.list(name, minPrice, maxPrice,
                pageable);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isSameAs(page);

        verify(productService).search(name, minPrice, maxPrice, pageable);
        verifyNoMoreInteractions(productService);
    }

    @Test
    void create_ShouldReturnCreatedWithBody() {

        ProductRequest request = mock(ProductRequest.class);
        ProductResponse created = mock(ProductResponse.class);

        when(productService.create(request)).thenReturn(created);

        ResponseEntity<ProductResponse> responseEntity = productController.create(request);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(responseEntity.getBody()).isSameAs(created);

        verify(productService).create(request);
        verifyNoMoreInteractions(productService);
    }

    @Test
    void update_ShouldReturnOkWithUpdatedProduct() {

        Long id = 1L;
        ProductRequest request = mock(ProductRequest.class);
        ProductResponse updated = mock(ProductResponse.class);

        when(productService.update(id, request)).thenReturn(updated);

        ResponseEntity<ProductResponse> responseEntity = productController.update(id, request);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isSameAs(updated);

        verify(productService).update(id, request);
        verifyNoMoreInteractions(productService);
    }
}
