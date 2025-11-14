package com.example.technicalTest.order.dto;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;

public record CreateOrderRequest(

        @NotEmpty(message = "El pedido debe tener al menos un producto")
        List<OrderItemRequest> items

) {}
