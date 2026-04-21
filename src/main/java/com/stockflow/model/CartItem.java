package com.stockflow.model;

import lombok.*;
import java.io.Serializable;

/**
 * Cart item stored in HTTP session (replaces IndexedDB cart store).
 * Must implement Serializable so Spring can serialize the session.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItem implements Serializable {

    private static final long serialVersionUID = 1L;

    private String productId;
    private String productName;
    private Double price;
    private Integer quantity;
    private String icon;

    public Double getLineTotal() {
        return price * quantity;
    }

    public void increment() {
        this.quantity++;
    }
}