package com.javatechie.pricing;

import com.javatechie.dto.Product;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Step 2a: the DEFAULT / fall-back behaviour (full price, no discount).
 *
 * This is the safe path that runs when the flag is OFF -- equivalent to the
 * "Noop" implementation in the Deltek doc's factory example.
 */
@Component
public class StandardPricing implements PricingStrategy {

    @Override
    public List<Product> apply(List<Product> products) {
        return products;
    }
}
