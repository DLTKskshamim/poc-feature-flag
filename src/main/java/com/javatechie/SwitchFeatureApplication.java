package com.javatechie;

import com.javatechie.dto.Product;
import com.javatechie.pricing.PricingStrategyFactory;
import com.javatechie.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@SpringBootApplication
@RestController
public class SwitchFeatureApplication {

    @Autowired
    private PricingStrategyFactory pricingStrategyFactory;

    @Autowired
    private InventoryService service;

    /**
     * Notice there is NO inline "if (flag) ... else ...". The factory owns the
     * flag decision and hands back the right pricing strategy; the controller
     * just runs it. This is the lightweight Factory Pattern in action.
     */
    @GetMapping("/orders")
    public List<Product> showAvailableProducts() {
        return pricingStrategyFactory.create().apply(service.getAllProducts());
    }

    public static void main(String[] args) {
        // --- Corporate TLS-interception fix (Deltek Prisma proxy) ----------------
        // On the Deltek network, HTTPS to LaunchDarkly is re-signed with the
        // "DeltekUSRoot" CA, which Windows trusts but the JVM does not -> the LD
        // SDK handshake fails with "PKIX path building failed". Using the Windows
        // trust store makes the JVM trust the same CAs as the OS. Must run before
        // the first HTTPS call (i.e. before the LDClient bean is created).
        boolean isWindows = System.getProperty("os.name", "").toLowerCase().contains("win");
        if (isWindows && System.getProperty("javax.net.ssl.trustStore") == null) {
            System.setProperty("javax.net.ssl.trustStoreType", "Windows-ROOT");
        }
        // ------------------------------------------------------------------------

        SpringApplication.run(SwitchFeatureApplication.class, args);
    }
}
