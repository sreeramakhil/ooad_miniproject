package com.stockflow.service.factory;

import com.stockflow.model.CartItem;
import com.stockflow.model.Order;
import com.stockflow.model.User;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NotificationFactoryTest {

    private final NotificationFactory notificationFactory = new NotificationFactory();

    @Test
    void shouldCreateUnreadNotificationWithOrderSummary() {
        Order order = Order.builder()
            .id("ORD-1001")
            .customer("user")
            .userEmail("user@example.com")
            .total(220.0)
            .build();

        User user = User.builder()
            .username("user")
            .email("user@example.com")
            .build();

        List<CartItem> cartItems = List.of(
            CartItem.builder().productId("PRD-001").productName("Mouse").price(100.0).quantity(2).icon("[item]").build()
        );

        var notification = notificationFactory.create(order, user, cartItems);

        assertTrue(notification.getId().startsWith("NOTIF-"));
        assertEquals("ORD-1001", notification.getOrderId());
        assertEquals("user", notification.getCustomer());
        assertEquals(220.0, notification.getTotal());
        assertTrue(notification.getItemsSummary().contains("Mouse"));
        assertFalse(notification.isRead());
    }
}
