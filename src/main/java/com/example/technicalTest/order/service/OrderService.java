package com.example.technicalTest.order.service;

import java.util.List;

import com.example.technicalTest.order.dto.CreateOrderRequest;
import com.example.technicalTest.order.dto.OrderResponse;

public interface OrderService {

    OrderResponse createOrder(String userEmail, CreateOrderRequest request);

    OrderResponse getOrderForUser(Long id, String userEmail);

    List<OrderResponse> getOrdersForUser(String userEmail);
}
