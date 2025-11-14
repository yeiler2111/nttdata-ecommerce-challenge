package com.example.technicalTest.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.technicalTest.product.entity.Product;
import com.example.technicalTest.product.repository.ProductRepository;
import com.example.technicalTest.user.entity.User;
import com.example.technicalTest.user.repository.UserRepository;
import com.example.technicalTest.user.utils.enums.Role;

@Configuration
public class DataLoader {

    @Bean
    CommandLineRunner initDatabase(
            UserRepository userRepository,
            ProductRepository productRepository,
            PasswordEncoder encoder) {
        return args -> {

            if (userRepository.findByEmail("admin@ntt.com").isEmpty()) {
                User admin = User.builder()
                        .name("Administrador")
                        .email("admin@ntt.com")
                        .password(encoder.encode("admin123"))
                        .role(Role.ROLE_ADMIN)
                        .build();

                userRepository.save(admin);
                System.out.println("Usuario ADMIN creado: admin@ntt.com / admin123");
            }

            if (userRepository.findByEmail("user@ntt.com").isEmpty()) {
                User normalUser = User.builder()
                        .name("Usuario Normal")
                        .email("user@ntt.com")
                        .password(encoder.encode("user123"))
                        .role(Role.ROLE_USER)
                        .build();

                userRepository.save(normalUser);
                System.out.println("Usuario USER creado: user@ntt.com / user123");
            }

            if (productRepository.count() == 0) {
                productRepository.save(Product.builder()
                        .name("Laptop Lenovo")
                        .description("Laptop de alta productividad")
                        .price(java.math.BigDecimal.valueOf(3500.00))
                        .stock(15)
                        .build());

                productRepository.save(Product.builder()
                        .name("Mouse Logitech")
                        .description("Mouse inalámbrico")
                        .price(java.math.BigDecimal.valueOf(120.00))
                        .stock(50)
                        .build());

                productRepository.save(Product.builder()
                        .name("Teclado Mecánico")
                        .description("Teclado RGB switches azules")
                        .price(java.math.BigDecimal.valueOf(220000.00))
                        .stock(20)
                        .build());

                System.out.println("Productos de prueba creados");
            }

        };
    }
}
