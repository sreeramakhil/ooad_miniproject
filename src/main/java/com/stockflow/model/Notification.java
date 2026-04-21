package com.stockflow.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {

    @Id
    @Column(length = 30)
    private String id; // NOTIF-<timestamp>

    @Column(nullable = false)
    private String orderId;

    @Column(nullable = false)
    private String customer;

    @Column(nullable = false)
    private String userEmail;

    @Column(nullable = false)
    private LocalDateTime date;

    @Column(nullable = false)
    private Double total;

    @Column(columnDefinition = "TEXT")
    private String itemsSummary; // Stored as formatted string

    @Column(nullable = false)
    @Builder.Default
    private boolean read = false;
}