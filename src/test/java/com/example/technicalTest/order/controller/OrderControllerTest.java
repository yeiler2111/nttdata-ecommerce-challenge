package com.example.technicalTest.order.controller;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.security.Principal;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.example.technicalTest.order.dto.CreateOrderRequest;
import com.example.technicalTest.order.dto.OrderResponse;
import com.example.technicalTest.order.service.OrderService;

class OrderControllerTest {

    @Mock
    private OrderService orderService;

    @Mock
    private Principal principal;

    @InjectMocks
    private OrderController orderController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createOrder_ShouldUsePrincipalEmailAndReturnCreated() {
        
        CreateOrderRequest request = mock(CreateOrderRequest.class);
        OrderResponse orderResponse = mock(OrderResponse.class);

        String email = "user@mail.com";
        when(principal.getName()).thenReturn(email);
        when(orderService.createOrder(email, request)).thenReturn(orderResponse);

        
        ResponseEntity<OrderResponse> responseEntity = orderController.createOrder(request, principal);

        
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(responseEntity.getBody()).isSameAs(orderResponse);

        verify(principal).getName();
        verify(orderService).createOrder(email, request);
        verifyNoMoreInteractions(orderService, principal);
    }

    @Test
    void getOrder_ShouldUsePrincipalEmailAndReturnOk() {
        
        Long orderId = 1L;
        OrderResponse orderResponse = mock(OrderResponse.class);
        String email = "user@mail.com";

        when(principal.getName()).thenReturn(email);
        when(orderService.getOrderForUser(orderId, email)).thenReturn(orderResponse);

        
        ResponseEntity<OrderResponse> responseEntity = orderController.getOrder(orderId, principal);

        
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isSameAs(orderResponse);

        verify(principal).getName();
        verify(orderService).getOrderForUser(orderId, email);
        verifyNoMoreInteractions(orderService, principal);
    }

    @Test
    void myOrders_ShouldReturnOrdersForPrincipalEmail() {
        
        String email = "user@mail.com";
        OrderResponse order1 = mock(OrderResponse.class);
        OrderResponse order2 = mock(OrderResponse.class);
        List<OrderResponse> orders = List.of(order1, order2);

        when(principal.getName()).thenReturn(email);
        when(orderService.getOrdersForUser(email)).thenReturn(orders);

        
        ResponseEntity<List<OrderResponse>> responseEntity = orderController.myOrders(principal);

        
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isSameAs(orders);

        verify(principal).getName();
        verify(orderService).getOrdersForUser(email);
        verifyNoMoreInteractions(orderService, principal);
    }
}
