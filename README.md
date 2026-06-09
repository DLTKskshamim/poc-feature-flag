# toggle-feature (LaunchDarkly)

A tiny Spring Boot demo of a feature flag controlling a 5% discount on `/orders`.
**Originally built with Togglz — migrated to LaunchDarkly, then hardened to SST standards
using a lightweight Factory Pattern.**

## What changed (Togglz → LaunchDarkly)

| Before (Togglz) | After (LaunchDarkly) |
|---|---|
| `togglz-spring-boot-starter` + `togglz-console` | `launchdarkly-java-server-sdk` |
| `FeatureManager.isActive(DISCOUNT_APPLIED)` | `featureFlagService.isEnabled(FeatureFlag.DISCOUNT_APPLIED)` |
| `NamedFeature` constant in the controller | central `FeatureFlag` enum registry |
| inline `if (flag) applyDiscount() else ...` | `PricingStrategyFactory.create().apply(...)` |
| flag state in `application.yml` / console | flag state controlled live from the LaunchDarkly dashboard |

## Architecture: lightweight Factory Pattern

SST's default approach uses a feature flag for *every* feature, so inline `if/else`
checks pile up and become hard to remove. A lightweight Factory keeps the flag
decision in **one place** and leaves call sites clean (no Branch-by-Abstraction
machinery needed):

```
/orders  →  PricingStrategyFactory.create()         ← the ONLY place the flag is read
                     │
        FeatureFlagService.isEnabled(FeatureFlag.DISCOUNT_APPLIED)
                     │
        flag ON  ─→ DiscountedPricing   (new behaviour: 5% off)
        flag OFF ─→ StandardPricing     (default/safe: full price)
                     │
              PricingStrategy.apply(products)
```

| File | Role |
|---|---|
| `featureflag/FeatureFlag.java` | central flag **registry** (enum): key + default + temporary/permanent |
| `service/FeatureFlagService.java` | the only class that calls the LD SDK |
| `config/LaunchDarklyConfig.java` | single `LDClient` bean (+ offline fallback, clean shutdown) |
| `pricing/PricingStrategy.java` | Step 1 — common interface |
| `pricing/StandardPricing.java` / `DiscountedPricing.java` | Step 2 — the two behaviours |
| `pricing/PricingStrategyFactory.java` | Step 3 — selects the strategy by flag |
| `SwitchFeatureApplication.java` | Step 4 — controller just runs the chosen strategy |

**Removing the flag later** (after 100% rollout) is localized: make the factory
always return `DiscountedPricing`, delete `StandardPricing`, archive the flag in LD.

## Hardening standards applied

1. **Central flag registry** — no raw flag-key strings anywhere; every flag is an
   enum entry with its key, safe default, and temporary/permanent intent. (Deltek
   "central class registry" rule.)
2. **Safe defaults everywhere** — each evaluation passes the registry's default, so a
   missing flag or an outage falls back to the safe path (full price), never an error.
3. **Graceful degradation** — if `LD_SDK_KEY` is unset, the client starts in **offline
   mode** (serves code defaults, boots instantly, no connection retries). This is what
   lets the build/tests run with no key.
4. **Secret hygiene** — the server SDK key is read from the `LD_SDK_KEY` env var and is
   never hardcoded or logged.
5. **Clean lifecycle** — `LDClient` is a single long-lived bean with
   `destroyMethod="close"` (flushes events on shutdown).
6. **No side effects** — `DiscountedPricing` returns new objects instead of mutating
   the input list.
7. **Provider isolation** — all LD calls sit behind `FeatureFlagService`; swapping
   providers later changes only that one class.
8. **Tested without the provider** — `PricingStrategyTest` (pure logic) and
   `PricingStrategyFactoryTest` (mocked flag) keep the build green offline.

## Run it

1. **Create the flag** in LaunchDarkly: key **`discount-applied`**, type **Boolean**.
2. **Set your SDK key and start:**
   ```powershell
   $env:LD_SDK_KEY = "sdk-xxxxxxxx-your-server-sdk-key"
   mvn spring-boot:run
   ```
   (No key? It still runs — in offline mode, full price.)
3. Hit `http://localhost:8080/orders`:
   - Flag **OFF** → full prices (mobile 50000, headphone 2000, watch 14999, glass 999).
   - Flag **ON** (toggle in the dashboard) → 5% off (mobile 47500, …), **no restart**.

## Tests

```powershell
mvn test
```
6 tests, no SDK key required.

## Notes

- **Corporate TLS:** on the Deltek network the JVM can't validate LaunchDarkly's cert
  (the Prisma proxy re-signs it with `DeltekUSRoot`, which Java doesn't trust by
  default). `main()` sets `javax.net.ssl.trustStoreType=Windows-ROOT` on Windows so the
  JVM trusts the same CAs as the OS. For Linux/CI, import `DeltekUSRoot` into the JVM
  truststore (or allowlist `*.launchdarkly.com` to skip TLS inspection).
- **Naming:** the demo key is `discount-applied`; a production headline flag would
  follow the Deltek convention, e.g. `com.deltek.TFS123.orders.discount`.
- Lombok was bumped to 1.18.34 (the Spring Boot 2.7.5 default fails to compile on JDK 21).
</content>
