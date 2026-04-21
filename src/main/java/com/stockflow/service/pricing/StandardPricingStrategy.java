package com.stockflow.service.pricing;

import com.stockflow.model.CartItem;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class StandardPricingStrategy implements PricingStrategy {

    private static final double TAX_RATE = 0.10;

    @Override
    public PricingSummary calculate(List<CartItem> cartItems) {
        double subtotal = cartItems.stream()
            .mapToDouble(CartItem::getLineTotal)
            .sum();
        double tax = subtotal * TAX_RATE;
        return new PricingSummary(subtotal, tax, subtotal + tax);
    }
}
