package com.javatechie.featureflag;

/**
 * Central registry of every feature flag in the application.
 *
 * HARDENING STANDARD: flag keys must NOT be scattered as raw strings across the
 * codebase. Every flag is declared here once, together with its safe default and
 * whether it is temporary (delete after rollout) or permanent (kill-switch/config).
 * This makes flags easy to find, audit, and remove -- the "central class registry"
 * rule from the Deltek Feature Flag standard.
 *
 * NAMING: the demo key is "discount-applied" for simplicity. A production headline
 * flag would follow the Deltek convention, e.g. "com.deltek.TFS123.orders.discount".
 */
public enum FeatureFlag {

    DISCOUNT_APPLIED(
            "discount-applied",   // key as defined in the LaunchDarkly dashboard
            false,                // safe default if LD is unreachable / flag missing
            true,                 // temporary (release) flag -> remove after rollout
            "Applies an order-level discount on /orders");

    private final String key;
    private final boolean defaultValue;
    private final boolean temporary;
    private final String description;

    FeatureFlag(String key, boolean defaultValue, boolean temporary, String description) {
        this.key = key;
        this.defaultValue = defaultValue;
        this.temporary = temporary;
        this.description = description;
    }

    public String getKey() {
        return key;
    }

    public boolean getDefaultValue() {
        return defaultValue;
    }

    public boolean isTemporary() {
        return temporary;
    }

    public String getDescription() {
        return description;
    }
}
