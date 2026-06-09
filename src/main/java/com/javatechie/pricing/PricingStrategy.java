package com.javatechie.pricing;

import com.javatechie.dto.Product;

import java.util.List;

/**
 * Step 1 of the Factory Pattern: a common interface for the two behaviours that
 * the feature flag switches between.
 *
 * Why a pattern instead of an inline {@code if (flagOn) ... else ...}?
 * The Deltek standard ("feature flags for every feature") means inline checks
 * pile up fast. A lightweight Factory keeps the flag decision in ONE place and
 * leaves call sites clean -- and removing the flag later is a localized change.
 */
public interface PricingStrategy {

    List<Product> apply(List<Product> products);
}
