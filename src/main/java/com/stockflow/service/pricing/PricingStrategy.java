package com.stockflow.service.pricing;

import com.stockflow.model.CartItem;

import java.util.List;

public interface PricingStrategy {

    PricingSummary calculate(List<CartItem> cartItems);
}
