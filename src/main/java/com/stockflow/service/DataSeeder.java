package com.stockflow.service;

import com.stockflow.model.*;
import com.stockflow.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepo;
    private final ProductRepository productRepo;
    private final OrderRepository orderRepo;
    private final PasswordEncoder encoder;

    public DataSeeder(UserRepository userRepo, ProductRepository productRepo,
                      OrderRepository orderRepo, PasswordEncoder encoder) {
        this.userRepo = userRepo;
        this.productRepo = productRepo;
        this.orderRepo = orderRepo;
        this.encoder = encoder;
    }

    @Override
    public void run(String... args) {
        seedUsers();
        seedProducts();
        seedOrders();
        System.out.println("✅ Database seeded successfully");
    }

    private void seedUsers() {
        if (userRepo.count() > 0) return;

        userRepo.saveAll(List.of(
            User.builder()
                .username("admin")
                .password(encoder.encode("password"))
                .email("turitoakhil123@gmail.com")
                .role("admin")
                .build(),
            User.builder()
                .username("user")
                .password(encoder.encode("password"))
                .email("user@example.com")
                .role("user")
                .build()
        ));
        System.out.println("  👤 Users seeded");
    }

    private void seedProducts() {
        if (productRepo.count() > 0) return;

        productRepo.saveAll(List.of(
            Product.builder().id("PRD-001").name("Wireless Mouse").price(29.99).stock(44).sales(0).icon("🖱️").build(),
            Product.builder().id("PRD-002").name("Mechanical Keyboard").price(89.99).stock(28).sales(0).icon("⌨️").build(),
            Product.builder().id("PRD-003").name("USB-C Hub").price(49.99).stock(64).sales(0).icon("🔌").build(),
            Product.builder().id("PRD-004").name("Laptop Stand").price(39.99).stock(8).sales(0).icon("💻").build(),
            Product.builder().id("PRD-005").name("Webcam HD").price(79.99).stock(12).sales(0).icon("📷").build(),
            Product.builder().id("PRD-006").name("Headphones").price(129.99).stock(0).sales(0).icon("🎧").build()
        ));
        System.out.println("  📦 Products seeded");
    }

    private void seedOrders() {
        if (orderRepo.count() > 0) return;

        Order order1 = Order.builder()
            .id("ORD-1001")
            .customer("alice")
            .userEmail("alice@example.com")
            .date(LocalDate.of(2024, 11, 15))
            .status("Shipped")
            .total(129.97)
            .build();

        OrderItem item1a = OrderItem.builder()
            .order(order1).productId("PRD-003").productName("USB-C Hub")
            .price(49.99).quantity(2).icon("🔌").build();
        OrderItem item1b = OrderItem.builder()
            .order(order1).productId("PRD-001").productName("Wireless Mouse")
            .price(29.99).quantity(1).icon("🖱️").build();
        order1.setItems(List.of(item1a, item1b));

        Order order2 = Order.builder()
            .id("ORD-1002")
            .customer("bob")
            .userEmail("bob@example.com")
            .date(LocalDate.of(2024, 11, 16))
            .status("Processing")
            .total(98.99)
            .build();

        OrderItem item2a = OrderItem.builder()
            .order(order2).productId("PRD-002").productName("Mechanical Keyboard")
            .price(89.99).quantity(1).icon("⌨️").build();
        order2.setItems(List.of(item2a));

        orderRepo.saveAll(List.of(order1, order2));
        System.out.println("  📋 Orders seeded");
    }
}