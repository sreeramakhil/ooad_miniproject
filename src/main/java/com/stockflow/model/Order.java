package com.stockflow.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

    @Id
    @Column(length = 20)
    private String id; // e.g. ORD-1001

    @Column(nullable = false)
    private String customer; // username

    @Column(nullable = false)
    private String userEmail;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    @Builder.Default
    private String status = "Processing";

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @Builder.Default
    private List<OrderItem> items = new ArrayList<>();

    @Column(nullable = false)
    private Double total;

    // Valid statuses
    public static final List<String> STATUSES = List.of(
        "Processing", "Confirmed", "Packed", "Dispatched",
        "Shipped", "Out for Delivery", "Delivered", "Cancelled"
    );

    public String getCssStatus() {
        return status.replace(" ", ".");
    }
}