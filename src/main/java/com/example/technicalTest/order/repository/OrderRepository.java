package com.example.technicalTest.order.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.technicalTest.order.entity.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserEmail(String email);
}

