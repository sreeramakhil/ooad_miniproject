package com.stockflow.service.factory;

import com.stockflow.model.CartItem;
import com.stockflow.model.Order;
import com.stockflow.model.OrderItem;
import com.stockflow.model.User;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class OrderFactory {

    public Order create(String orderId, User user, List<CartItem> cartItems, double total) {
        Order order = Order.builder()
            .id(orderId)
            .customer(user.getUsername())
            .userEmail(user.getEmail())
            .date(LocalDate.now())
            .status("Processing")
            .total(total)
            .build();

        List<OrderItem> items = cartItems.stream()
            .map(cartItem -> OrderItem.builder()
                .order(order)
                .productId(cartItem.getProductId())
                .productName(cartItem.getProductName())
                .price(cartItem.getPrice())
                .quantity(cartItem.getQuantity())
                .icon(cartItem.getIcon())
                .build())
            .toList();

        order.setItems(items);
        return order;
    }
}
