package com.example.technicalTest.order.service;

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
import org.mockito.MockitoAnnotations;

import com.example.technicalTest.order.dto.CreateOrderRequest;
import com.example.technicalTest.order.dto.OrderItemRequest;
import com.example.technicalTest.order.dto.OrderResponse;
import com.example.technicalTest.order.entity.Order;
import com.example.technicalTest.order.repository.OrderItemRepository;
import com.example.technicalTest.order.repository.OrderRepository;
import com.example.technicalTest.product.entity.Product;
import com.example.technicalTest.product.repository.ProductRepository;
import com.example.technicalTest.user.entity.User;
import com.example.technicalTest.user.repository.UserRepository;

class OrderServiceImplTest {

    @Mock private OrderRepository orderRepository;
    @Mock private OrderItemRepository itemRepository;
    @Mock private UserRepository userRepository;
    @Mock private ProductRepository productRepository;

    @InjectMocks
    private OrderServiceImpl orderService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createOrder_ShouldCreate_WhenValid() {
        User user = new User();
        user.setEmail("test@mail.com");

        Product p = new Product();
        p.setId(1L);
        p.setName("Phone");
        p.setStock(10);
        p.setPrice(BigDecimal.TEN);

        CreateOrderRequest req = new CreateOrderRequest(
                List.of(new OrderItemRequest(1L, 2))
        );

        when(userRepository.findByEmail("test@mail.com"))
                .thenReturn(Optional.of(user));

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(p));

        when(orderRepository.save(any(Order.class)))
                .thenAnswer(invocation -> {
                    Order o = invocation.getArgument(0);
                    o.setId(99L);
                    return o;
                });

        OrderResponse response = orderService.createOrder("test@mail.com", req);

        assertThat(response.id()).isEqualTo(99L);
        verify(itemRepository).saveAll(anyList());
    }

    @Test
    void createOrder_ShouldThrow_WhenUserNotFound() {
        CreateOrderRequest req = new CreateOrderRequest(
                List.of(new OrderItemRequest(1L, 2))
        );

        when(userRepository.findByEmail("test@mail.com"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.createOrder("test@mail.com", req))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Usuario no encontrado");
    }

    @Test
    void createOrder_ShouldThrow_WhenProductNotFound() {
        User user = new User();
        user.setEmail("test@mail.com");

        when(userRepository.findByEmail("test@mail.com"))
                .thenReturn(Optional.of(user));

        when(productRepository.findById(1L))
                .thenReturn(Optional.empty());

        CreateOrderRequest req = new CreateOrderRequest(
                List.of(new OrderItemRequest(1L, 2))
        );

        assertThatThrownBy(() -> orderService.createOrder("test@mail.com", req))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Producto no encontrado");
    }

    @Test
    void createOrder_ShouldThrow_WhenStockInsufficient() {
        User user = new User();
        user.setEmail("test@mail.com");

        Product p = new Product();
        p.setStock(1); 

        when(userRepository.findByEmail("test@mail.com"))
                .thenReturn(Optional.of(user));

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(p));

        CreateOrderRequest req = new CreateOrderRequest(
                List.of(new OrderItemRequest(1L, 3))
        );

        assertThatThrownBy(() -> orderService.createOrder("test@mail.com", req))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Stock insuficiente");
    }

    @Test
    void getOrderForUser_ShouldReturn_WhenValid() {
        User user = new User();
        user.setEmail("test@mail.com");

        Order order = new Order();
        order.setId(10L);
        order.setUser(user);
        order.setItems(List.of());

        when(orderRepository.findById(10L)).thenReturn(Optional.of(order));

        OrderResponse res = orderService.getOrderForUser(10L, "test@mail.com");

        assertThat(res.id()).isEqualTo(10L);
    }

    @Test
    void getOrderForUser_ShouldThrow_WhenDifferentUser() {
        User user = new User();
        user.setEmail("owner@mail.com");

        Order order = new Order();
        order.setUser(user);
        order.setItems(List.of());

        when(orderRepository.findById(1L))
                .thenReturn(Optional.of(order));

        assertThatThrownBy(() -> orderService.getOrderForUser(1L, "other@mail.com"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("No tienes permiso");
    }
}
