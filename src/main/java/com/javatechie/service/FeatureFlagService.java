package com.javatechie.service;

import com.javatechie.featureflag.FeatureFlag;
import com.launchdarkly.sdk.LDContext;
import com.launchdarkly.sdk.server.LDClient;
import org.springframework.stereotype.Service;

/**
 * Thin wrapper around the LaunchDarkly SDK -- the direct replacement for
 * Togglz's {@code FeatureManager.isActive(feature)}.
 *
 * HARDENING STANDARD: this is the ONLY class that touches the LD SDK, and it only
 * accepts {@link FeatureFlag} registry entries (never raw strings). The safe
 * default and key both come from the registry, so an evaluation can never use an
 * unknown key or an unsafe fallback.
 */
@Service
public class FeatureFlagService {

    private final LDClient ldClient;

    public FeatureFlagService(LDClient ldClient) {
        this.ldClient = ldClient;
    }

    public boolean isEnabled(FeatureFlag flag) {
        return ldClient.boolVariation(flag.getKey(), defaultContext(), flag.getDefaultValue());
    }

    /**
     * A "context" describes who we evaluate the flag for. This simple demo uses a
     * single anonymous user; a real app would build a multi-context
     * (user + tenant + version) so flags can be targeted per tenant.
     */
    private LDContext defaultContext() {
        return LDContext.builder("anonymous-user")
                .kind("user")
                .anonymous(true)
                .build();
    }
}
