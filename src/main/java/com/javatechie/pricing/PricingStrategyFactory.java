package com.javatechie.pricing;

import com.javatechie.featureflag.FeatureFlag;
import com.javatechie.service.FeatureFlagService;
import org.springframework.stereotype.Component;

/**
 * Step 3 of the Factory Pattern: the factory decides WHICH strategy to use, based
 * on the feature flag. This is the single place the flag is consulted for pricing.
 */
@Component
public class PricingStrategyFactory {

    private final FeatureFlagService featureFlagService;
    private final DiscountedPricing discountedPricing;
    private final StandardPricing standardPricing;

    public PricingStrategyFactory(FeatureFlagService featureFlagService,
                                  DiscountedPricing discountedPricing,
                                  StandardPricing standardPricing) {
        this.featureFlagService = featureFlagService;
        this.discountedPricing = discountedPricing;
        this.standardPricing = standardPricing;
    }

    /** @return the pricing strategy that matches the current flag state. */
    public PricingStrategy create() {
        return featureFlagService.isEnabled(FeatureFlag.DISCOUNT_APPLIED)
                ? discountedPricing
                : standardPricing;
    }
}
