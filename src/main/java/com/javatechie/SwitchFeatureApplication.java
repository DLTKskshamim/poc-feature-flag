package com.javatechie;

import com.javatechie.dto.Product;
import com.javatechie.service.FeatureFlagService;
import com.javatechie.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
@RestController
public class SwitchFeatureApplication {
    @Autowired
    private FeatureFlagService featureFlagService;

    @Autowired
    private InventoryService service;

    // The LaunchDarkly flag key (create this flag in the LaunchDarkly dashboard).
    @Value("${launchdarkly.flag.discount-applied:discount-applied}")
    private String discountFlagKey;

    @GetMapping("/orders")
    public List<Product> showAvailableProducts() {
        if (featureFlagService.isEnabled(discountFlagKey)) {
            return applyDiscount(service.getAllProducts());
        } else {
            return service.getAllProducts();
        }
    }

    private List<Product> applyDiscount(List<Product> availableProducts) {
        List<Product> orderListAfterDiscount = new ArrayList<>();
        availableProducts.forEach(order -> {
            order.setPrice(order.getPrice() - (order.getPrice() * 5 / 100));
            orderListAfterDiscount.add(order);
        });
        return orderListAfterDiscount;
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
