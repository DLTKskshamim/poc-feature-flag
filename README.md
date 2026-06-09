# toggle-feature (LaunchDarkly)

A tiny Spring Boot demo of a feature flag controlling a 5% discount on `/orders`.
**Originally built with Togglz — now migrated to LaunchDarkly.**

## What changed (Togglz → LaunchDarkly)

| Before (Togglz) | After (LaunchDarkly) |
|---|---|
| `togglz-spring-boot-starter` + `togglz-console` deps | `launchdarkly-java-server-sdk` dep |
| `FeatureManager.isActive(DISCOUNT_APPLIED)` | `featureFlagService.isEnabled("discount-applied")` |
| `NamedFeature DISCOUNT_APPLIED` constant | flag key `discount-applied` in the LaunchDarkly dashboard |
| flag state in `application.yml` / console | flag state controlled from the LaunchDarkly dashboard (live) |

New files:
- `config/LaunchDarklyConfig.java` — one long-lived `LDClient` bean (replaces `FeatureManager`).
- `service/FeatureFlagService.java` — thin wrapper: `isEnabled(flagKey)`.

The `/orders` logic is unchanged: flag ON → apply 5% discount; flag OFF → full price.

## Run it

1. **Create the flag** in LaunchDarkly: key **`discount-applied`**, type **Boolean**.
2. **Set your server-side SDK key** and start the app:

   ```powershell
   $env:LD_SDK_KEY = "sdk-xxxxxxxx-your-server-sdk-key"
   mvn spring-boot:run
   ```
3. Hit the endpoint:
   ```
   http://localhost:8080/orders
   ```
   - Flag **OFF** → full prices (mobile 50000, headphone 2000, watch 14999, glass 999).
   - Flag **ON** (toggle in the LD dashboard) → 5% off (mobile 47500, headphone 1900, …), **no restart needed**.

## Notes

- **Server SDK key is a secret** — it's read from the `LD_SDK_KEY` env var (see `application.yml`); don't hardcode it.
- **Corporate TLS note:** on the Deltek network the JVM can't validate LaunchDarkly's
  cert (the Prisma proxy re-signs it with `DeltekUSRoot`, which Java doesn't trust by
  default). `main()` sets `javax.net.ssl.trustStoreType=Windows-ROOT` on Windows so the
  JVM trusts the same CAs as the OS. For Linux/CI, import `DeltekUSRoot` into the JVM
  truststore instead (or allowlist `*.launchdarkly.com` to skip TLS inspection).
- Lombok was bumped to 1.18.34 (the Spring Boot 2.7.5 default, 1.18.24, fails to compile on JDK 21).
</content>
