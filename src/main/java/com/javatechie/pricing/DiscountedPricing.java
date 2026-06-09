package com.javatechie.pricing;

import com.javatechie.dto.Product;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Step 2b: the NEW behaviour gated by the flag (5% discount).
 *
 * Note it returns NEW Product objects rather than mutating the input list -- a
 * small hardening improvement so callers never see surprising side effects.
 */
@Component
public class DiscountedPricing implements PricingStrategy {

    private static final double DISCOUNT_PERCENT = 5;

    @Override
    public List<Product> apply(List<Product> products) {
        List<Product> discounted = new ArrayList<>();
        products.forEach(p -> {
            double newPrice = p.getPrice() - (p.getPrice() * DISCOUNT_PERCENT / 100);
            discounted.add(new Product(p.getId(), p.getName(), newPrice));
        });
        return discounted;
    }
}
