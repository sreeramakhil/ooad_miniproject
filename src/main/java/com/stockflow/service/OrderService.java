package com.stockflow.service;

import com.stockflow.model.CartItem;
import com.stockflow.model.Notification;
import com.stockflow.model.Order;
import com.stockflow.model.Product;
import com.stockflow.model.User;
import com.stockflow.repository.NotificationRepository;
import com.stockflow.repository.OrderRepository;
import com.stockflow.repository.ProductRepository;
import com.stockflow.service.factory.NotificationFactory;
import com.stockflow.service.factory.OrderFactory;
import com.stockflow.service.pricing.PricingStrategy;
import com.stockflow.service.pricing.PricingSummary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class OrderService {

    private final OrderRepository orderRepo;
    private final ProductRepository productRepo;
    private final NotificationRepository notifRepo;
    private final PricingStrategy pricingStrategy;
    private final OrderFactory orderFactory;
    private final NotificationFactory notificationFactory;

    public OrderService(OrderRepository orderRepo, ProductRepository productRepo,
                        NotificationRepository notifRepo, PricingStrategy pricingStrategy,
                        OrderFactory orderFactory, NotificationFactory notificationFactory) {
        this.orderRepo = orderRepo;
        this.productRepo = productRepo;
        this.notifRepo = notifRepo;
        this.pricingStrategy = pricingStrategy;
        this.orderFactory = orderFactory;
        this.notificationFactory = notificationFactory;
    }

    public List<Order> getAllOrders() {
        return orderRepo.findAllByOrderByDateDesc();
    }

    public List<Order> getOrdersByCustomer(String username) {
        return orderRepo.findByCustomerOrderByDateDesc(username);
    }

    public Optional<Order> getById(String id) {
        return orderRepo.findById(id);
    }

    public Order placeOrder(User user, List<CartItem> cartItems) {
        for (CartItem item : cartItems) {
            Product product = productRepo.findById(item.getProductId())
                .orElseThrow(() -> new IllegalStateException("Product not found: " + item.getProductId()));
            if (product.getStock() < item.getQuantity()) {
                throw new IllegalStateException("Insufficient stock for: " + item.getProductName());
            }
        }

        PricingSummary pricingSummary = pricingStrategy.calculate(cartItems);
        String orderId = generateOrderId();
        Order order = orderFactory.create(orderId, user, cartItems, pricingSummary.total());
        orderRepo.save(order);

        cartItems.forEach(cartItem -> {
            productRepo.findById(cartItem.getProductId()).ifPresent(product -> {
                product.setStock(product.getStock() - cartItem.getQuantity());
                product.setSales(product.getSales() + cartItem.getQuantity());
                productRepo.save(product);
            });
        });

        Notification notification = notificationFactory.create(order, user, cartItems);
        notifRepo.save(notification);

        return order;
    }

    public Optional<Order> updateStatus(String orderId, String newStatus) {
        return orderRepo.findById(orderId).map(order -> {
            order.setStatus(newStatus);
            return orderRepo.save(order);
        });
    }

    public long countOrders() {
        return orderRepo.count();
    }

    private String generateOrderId() {
        long count = orderRepo.count() + 1001;
        String id = "ORD-" + count;
        while (orderRepo.existsById(id)) {
            count++;
            id = "ORD-" + count;
        }
        return id;
    }
}
