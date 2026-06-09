package com.javatechie.config;

import com.launchdarkly.sdk.server.LDClient;
import com.launchdarkly.sdk.server.LDConfig;
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
 * HARDENING STANDARDS applied here:
 *  - destroyMethod = "close": the client is shut down cleanly on app shutdown,
 *    flushing pending analytics events.
 *  - graceful degradation: if no SDK key is configured (local dev / tests / CI),
 *    the client starts in OFFLINE mode so the app still boots instantly and every
 *    flag serves its code default -- instead of hanging on connection retries.
 *  - the secret SDK key is read from configuration (env var) and never logged.
 */
@Configuration
public class LaunchDarklyConfig {

    private static final Logger log = LoggerFactory.getLogger(LaunchDarklyConfig.class);

    @Bean(destroyMethod = "close")
    public LDClient ldClient(@Value("${launchdarkly.sdk.key:}") String sdkKey) {
        if (sdkKey == null || sdkKey.isBlank()) {
            log.warn("LD_SDK_KEY is not set -> starting LaunchDarkly in OFFLINE mode. "
                    + "All flags will serve their code defaults. Set LD_SDK_KEY to connect.");
            LDConfig offline = new LDConfig.Builder().offline(true).build();
            return new LDClient("offline-placeholder", offline);
        }

        LDClient client = new LDClient(sdkKey);
        log.info("LaunchDarkly client initialized = {}", client.isInitialized());
        return client;
    }
}
