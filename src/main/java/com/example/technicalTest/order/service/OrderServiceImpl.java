package com.example.technicalTest.order.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.technicalTest.order.dto.CreateOrderRequest;
import com.example.technicalTest.order.dto.OrderItemRequest;
import com.example.technicalTest.order.dto.OrderItemResponse;
import com.example.technicalTest.order.dto.OrderResponse;
import com.example.technicalTest.order.entity.Order;
import com.example.technicalTest.order.entity.OrderItem;
import com.example.technicalTest.order.repository.OrderItemRepository;
import com.example.technicalTest.order.repository.OrderRepository;
import com.example.technicalTest.product.entitie.Product;
import com.example.technicalTest.product.repository.ProductRepository;
import com.example.technicalTest.user.entity.User;
import com.example.technicalTest.user.repository.UserRepository;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public OrderServiceImpl(
            OrderRepository orderRepository,
            OrderItemRepository orderItemRepository,
            UserRepository userRepository,
            ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    @Override
    @Transactional
    public OrderResponse createOrder(String userEmail, CreateOrderRequest request) {

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Order order = new Order();
        order.setUser(user);

        List<OrderItem> items = new ArrayList<>();

        for (OrderItemRequest itemReq : request.items()) {

            Product product = productRepository.findById(itemReq.productId())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado: " + itemReq.productId()));

            if (product.getStock() < itemReq.quantity()) {
                throw new RuntimeException("Stock insuficiente para el producto: " + product.getName());
            }

            product.setStock(product.getStock() - itemReq.quantity());

            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setProduct(product);
            item.setQuantity(itemReq.quantity());
            item.setUnitPrice(product.getPrice());

            items.add(item);
        }

        order.setItems(items);

        Order savedOrder = orderRepository.save(order);

        orderItemRepository.saveAll(items);

        return toResponse(savedOrder);
    }

    @Override
    public OrderResponse getOrderForUser(Long id, String userEmail) {

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));

        if (!order.getUser().getEmail().equals(userEmail)) {
            throw new RuntimeException("No tienes permiso para ver este pedido");
        }

        return toResponse(order);
    }

    @Override
    public List<OrderResponse> getOrdersForUser(String userEmail) {
        List<Order> orders = orderRepository.findByUserEmail(userEmail);

        return orders.stream()
                .map(this::toResponse)
                .toList();
    }

    private OrderResponse toResponse(Order order) {

        List<OrderItemResponse> itemResponses = order.getItems().stream()
                .map(item -> new OrderItemResponse(
                        item.getProduct().getId(),
                        item.getProduct().getName(),
                        item.getQuantity(),
                        item.getUnitPrice()))
                .toList();

        return new OrderResponse(
                order.getId(),
                order.getTotal(),
                order.getCreatedAt(),
                itemResponses);
    }
}
