package com.stockflow.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @Column(length = 20)
    private String id; // e.g. PRD-001

    @Column(nullable = false)
    @NotBlank(message = "Product name is required")
    @Size(min = 3, message = "Product name must be at least 3 characters")
    private String name;

    @Column(nullable = false)
    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    private Double price;

    @Column(nullable = false)
    @NotNull(message = "Stock is required")
    @Min(value = 0, message = "Stock cannot be negative")
    private Integer stock;

    @Column(nullable = false)
    @Builder.Default
    private Integer sales = 0;

    @Column(length = 10)
    @Builder.Default
    private String icon = "📦";

    // ── Derived helpers ──────────────────────────────────────────────────
    public String getStockStatus() {
        if (stock > 10) return "good";
        if (stock > 0)  return "low";
        return "out";
    }

    public String getStockLabel() {
        if (stock > 0) return "In Stock: " + stock;
        return "Out of Stock";
    }

    public boolean isInStock() {
        return stock > 0;
    }
}