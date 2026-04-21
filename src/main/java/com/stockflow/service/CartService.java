package com.stockflow.service;

import com.stockflow.model.CartItem;
import com.stockflow.model.Product;
import com.stockflow.repository.ProductRepository;
import com.stockflow.service.pricing.PricingStrategy;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CartService {

    private static final String CART_KEY = "CART";

    private final ProductRepository productRepo;
    private final PricingStrategy pricingStrategy;

    public CartService(ProductRepository productRepo, PricingStrategy pricingStrategy) {
        this.productRepo = productRepo;
        this.pricingStrategy = pricingStrategy;
    }

    @SuppressWarnings("unchecked")
    public List<CartItem> getCart(HttpSession session) {
        List<CartItem> cart = (List<CartItem>) session.getAttribute(CART_KEY);
        if (cart == null) {
            cart = new ArrayList<>();
            session.setAttribute(CART_KEY, cart);
        }
        return cart;
    }

    public String addToCart(HttpSession session, String productId) {
        Optional<Product> productOpt = productRepo.findById(productId);
        if (productOpt.isEmpty()) return "Product not found";

        Product product = productOpt.get();
        if (product.getStock() == 0) return "Product is out of stock";

        List<CartItem> cart = getCart(session);

        // Check if already in cart
        Optional<CartItem> existing = cart.stream()
            .filter(ci -> ci.getProductId().equals(productId))
            .findFirst();

        if (existing.isPresent()) {
            CartItem item = existing.get();
            if (item.getQuantity() >= product.getStock()) {
                return "Not enough stock available";
            }
            item.increment();
        } else {
            cart.add(CartItem.builder()
                .productId(product.getId())
                .productName(product.getName())
                .price(product.getPrice())
                .quantity(1)
                .icon(product.getIcon())
                .build());
        }
        return null; // null = success
    }

    public void removeFromCart(HttpSession session, String productId) {
        List<CartItem> cart = getCart(session);
        cart.removeIf(ci -> ci.getProductId().equals(productId));
    }

    public void clearCart(HttpSession session) {
        session.removeAttribute(CART_KEY);
    }

    public int getCartSize(HttpSession session) {
        return getCart(session).size();
    }

    public double getSubtotal(HttpSession session) {
        return pricingStrategy.calculate(getCart(session)).subtotal();
    }

    public double getTax(HttpSession session) {
        return pricingStrategy.calculate(getCart(session)).tax();
    }

    public double getTotal(HttpSession session) {
        return pricingStrategy.calculate(getCart(session)).total();
    }
}
