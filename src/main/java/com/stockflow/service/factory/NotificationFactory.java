package com.stockflow.service.factory;

import com.stockflow.model.CartItem;
import com.stockflow.model.Notification;
import com.stockflow.model.Order;
import com.stockflow.model.User;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class NotificationFactory {

    public Notification create(Order order, User user, List<CartItem> cartItems) {
        String itemsSummary = cartItems.stream()
            .map(cartItem -> cartItem.getIcon() + " " + cartItem.getProductName() + " x " + cartItem.getQuantity()
                + " - $" + String.format("%.2f", cartItem.getLineTotal()))
            .collect(Collectors.joining("\n"));

        return Notification.builder()
            .id("NOTIF-" + System.currentTimeMillis())
            .orderId(order.getId())
            .customer(user.getUsername())
            .userEmail(user.getEmail())
            .date(LocalDateTime.now())
            .total(order.getTotal())
            .itemsSummary(itemsSummary)
            .read(false)
            .build();
    }
}
