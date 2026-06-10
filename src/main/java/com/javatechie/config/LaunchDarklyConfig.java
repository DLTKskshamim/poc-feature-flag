package com.javatechie.config;

import com.launchdarkly.sdk.server.LDClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Creates one long-lived LDClient for the whole application (replaces Togglz's
 * FeatureManager). The SDK keeps flag data in memory via a streaming connection,
 * so evaluating a flag is a fast local lookup -- create the client ONCE.
 *
 * destroyMethod = "close" -> client is shut down cleanly on app shutdown,
 * flushing any pending analytics events.
 */
@Configuration
public class LaunchDarklyConfig {

    private static final Logger log = LoggerFactory.getLogger(LaunchDarklyConfig.class);

    @Bean(destroyMethod = "close")
    public LDClient ldClient(@Value("${launchdarkly.sdk.key:}") String sdkKey) {
        if (sdkKey == null || sdkKey.isBlank()) {
            log.warn("LD_SDK_KEY is not set. Flags will fall back to their default value (false).");
        }
        LDClient client = new LDClient(sdkKey);
        log.info("LaunchDarkly client initialized = {}", client.isInitialized());
        return client;
    }
}
