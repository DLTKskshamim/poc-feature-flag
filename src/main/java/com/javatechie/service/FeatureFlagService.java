package com.javatechie.service;

import com.launchdarkly.sdk.LDContext;
import com.launchdarkly.sdk.server.LDClient;
import org.springframework.stereotype.Service;

/**
 * Thin wrapper around the LaunchDarkly SDK -- the direct replacement for
 * Togglz's {@code FeatureManager.isActive(feature)}.
 *
 * Keeping all LaunchDarkly calls here means the rest of the app stays clean and
 * provider-agnostic (swapping providers later = change only this class).
 */
@Service
public class FeatureFlagService {

    private final LDClient ldClient;

    public FeatureFlagService(LDClient ldClient) {
        this.ldClient = ldClient;
    }

    /**
     * @param flagKey the flag's key as defined in the LaunchDarkly dashboard
     * @return true if the flag is ON for the current context, false otherwise
     *         (false is also the safe default if LaunchDarkly is unreachable)
     */
    public boolean isEnabled(String flagKey) {
        // A "context" describes who we evaluate the flag for. This simple demo
        // uses a single anonymous user; a real app would build a richer context
        // (user + tenant + version) so flags can be targeted per tenant.
        LDContext context = LDContext.builder("anonymous-user")
                .kind("user")
                .anonymous(true)
                .build();

        return ldClient.boolVariation(flagKey, context, false);
    }
}
