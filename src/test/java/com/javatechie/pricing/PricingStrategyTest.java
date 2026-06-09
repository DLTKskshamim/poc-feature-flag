package com.javatechie.pricing;

import com.javatechie.dto.Product;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Pure unit tests for the two strategies -- no Spring, no LaunchDarkly, no network.
 * HARDENING STANDARD: the behaviour gated by a flag must be testable without the
 * flag provider being available, so the build stays green offline / in CI.
 */
class PricingStrategyTest {

    private List<Product> sampleProducts() {
        return List.of(new Product(1, "mobile", 50000), new Product(2, "glass", 1000));
    }

    @Test
    void standardPricingKeepsFullPrice() {
        List<Product> result = new StandardPricing().apply(sampleProducts());
        assertThat(result).extracting(Product::getPrice).containsExactly(50000.0, 1000.0);
    }

    @Test
    void discountedPricingApplies5Percent() {
        List<Product> result = new DiscountedPricing().apply(sampleProducts());
        assertThat(result).extracting(Product::getPrice).containsExactly(47500.0, 950.0);
    }

    @Test
    void discountedPricingDoesNotMutateInput() {
        List<Product> input = sampleProducts();
        new DiscountedPricing().apply(input);
        assertThat(input).extracting(Product::getPrice).containsExactly(50000.0, 1000.0);
    }
}
