package com.javatechie.service;
import com.javatechie.config.LaunchDarklyConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.launchdarkly.sdk.LDContext;
import com.launchdarkly.sdk.server.LDClient;
import org.springframework.stereotype.Service;

/**
 * Keeping all LaunchDarkly calls here means the rest of the app stays clean and
 * provider-agnostic (swapping providers later = change only this class).
 */
@Service
public class FeatureFlagService {

    private static final Logger log = LoggerFactory.getLogger(LaunchDarklyConfig.class);
    private final LDClient ldClient;

    public FeatureFlagService(LDClient ldClient) {
        this.ldClient = ldClient;
    }

    public boolean isEnabled(String flagKey) {
        // A "context" describes who we evaluate the flag for.
        LDContext context = LDContext.builder("new-test-key")
                .kind("user")
                .name("user1")
                .build();
        log.info("Evaluating flag: {}", flagKey);
        return ldClient.boolVariation(flagKey, context, false);
    }
}
