package com.javatechie.pricing;

import com.javatechie.featureflag.FeatureFlag;
import com.javatechie.service.FeatureFlagService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Verifies the Factory selects the right strategy for each flag state -- using a
 * mocked FeatureFlagService so no real LaunchDarkly connection is needed.
 */
class PricingStrategyFactoryTest {

    private PricingStrategyFactory factoryWithFlag(boolean enabled) {
        FeatureFlagService ff = Mockito.mock(FeatureFlagService.class);
        Mockito.when(ff.isEnabled(FeatureFlag.DISCOUNT_APPLIED)).thenReturn(enabled);
        return new PricingStrategyFactory(ff, new DiscountedPricing(), new StandardPricing());
    }

    @Test
    void usesDiscountedPricingWhenFlagOn() {
        assertThat(factoryWithFlag(true).create()).isInstanceOf(DiscountedPricing.class);
    }

    @Test
    void usesStandardPricingWhenFlagOff() {
        assertThat(factoryWithFlag(false).create()).isInstanceOf(StandardPricing.class);
    }
}
