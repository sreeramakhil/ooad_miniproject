package com.stockflow.service.pricing;

import com.stockflow.model.CartItem;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StandardPricingStrategyTest {

    private final StandardPricingStrategy pricingStrategy = new StandardPricingStrategy();

    @Test
    void shouldCalculateSubtotalTaxAndTotal() {
        List<CartItem> cartItems = List.of(
            CartItem.builder().productId("PRD-001").productName("Mouse").price(100.0).quantity(2).build(),
            CartItem.builder().productId("PRD-002").productName("Keyboard").price(50.0).quantity(1).build()
        );

        PricingSummary summary = pricingStrategy.calculate(cartItems);

        assertEquals(250.0, summary.subtotal());
        assertEquals(25.0, summary.tax());
        assertEquals(275.0, summary.total());
    }
}
