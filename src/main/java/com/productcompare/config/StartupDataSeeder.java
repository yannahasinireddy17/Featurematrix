package com.productcompare.config;

import com.productcompare.entity.Feature;
import com.productcompare.entity.FeatureValue;
import com.productcompare.entity.Product;
import com.productcompare.entity.UserWorkspace;
import com.productcompare.repository.FeatureRepository;
import com.productcompare.repository.FeatureValueRepository;
import com.productcompare.repository.ProductRepository;
import com.productcompare.repository.UserWorkspaceRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class StartupDataSeeder implements CommandLineRunner {

    private final UserWorkspaceRepository userWorkspaceRepository;
    private final ProductRepository productRepository;
    private final FeatureRepository featureRepository;
    private final FeatureValueRepository featureValueRepository;
    private final PasswordEncoder passwordEncoder;

    public StartupDataSeeder(
            UserWorkspaceRepository userWorkspaceRepository,
            ProductRepository productRepository,
            FeatureRepository featureRepository,
            FeatureValueRepository featureValueRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userWorkspaceRepository = userWorkspaceRepository;
        this.productRepository = productRepository;
        this.featureRepository = featureRepository;
        this.featureValueRepository = featureValueRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        UserWorkspace demoWorkspace = userWorkspaceRepository.findByUsername("demo")
                .orElseGet(this::createDemoWorkspace);

        if (demoWorkspace.getPasswordHash() == null || demoWorkspace.getPasswordHash().isBlank()) {
            demoWorkspace.setPasswordHash(passwordEncoder.encode("demo123"));
            if (demoWorkspace.getCreatedAt() == null) {
                demoWorkspace.setCreatedAt(LocalDateTime.now());
            }
            demoWorkspace = userWorkspaceRepository.save(demoWorkspace);
        }

        Map<String, Product> productsByName = ensureProducts(demoWorkspace);
        Map<String, Feature> featuresByName = ensureFeatures(demoWorkspace);
        ensureFeatureValues(productsByName, featuresByName);
    }

    private UserWorkspace createDemoWorkspace() {
        UserWorkspace workspace = new UserWorkspace();
        workspace.setUsername("demo");
        workspace.setPasswordHash(passwordEncoder.encode("demo123"));
        workspace.setCreatedAt(LocalDateTime.now());
        return userWorkspaceRepository.save(workspace);
    }

    private Map<String, Product> ensureProducts(UserWorkspace workspace) {
        List<String> desiredProducts = List.of("Phone A", "Phone B", "Laptop C");
        Map<String, Product> existing = productRepository.findByWorkspaceIdOrderByNameAsc(workspace.getId())
                .stream()
                .collect(Collectors.toMap(Product::getName, product -> product));

        for (String productName : desiredProducts) {
            if (!existing.containsKey(productName)) {
                Product product = new Product();
                product.setName(productName);
                product.setWorkspace(workspace);
                existing.put(productName, productRepository.save(product));
            }
        }
        return existing;
    }

    private Map<String, Feature> ensureFeatures(UserWorkspace workspace) {
        List<String> desiredFeatures = List.of(
                "Price",
            "Purchase Link",
                "Battery",
                "RAM",
                "Storage",
                "Camera",
                "Display",
                "Processor",
                "Operating System"
        );

        Map<String, Feature> existing = featureRepository.findByWorkspaceIdOrderByNameAsc(workspace.getId())
                .stream()
                .collect(Collectors.toMap(Feature::getName, feature -> feature));

        for (String featureName : desiredFeatures) {
            if (!existing.containsKey(featureName)) {
                Feature feature = new Feature();
                feature.setName(featureName);
                feature.setImportance(1);
                feature.setWorkspace(workspace);
                existing.put(featureName, featureRepository.save(feature));
            }
        }
        return existing;
    }

    private void ensureFeatureValues(Map<String, Product> productsByName, Map<String, Feature> featuresByName) {
        Map<String, Map<String, String>> comparisonData = new LinkedHashMap<>();

        comparisonData.put("Phone A", Map.of(
                "Price", "$699",
            "Purchase Link", "https://www.amazon.in/",
                "Battery", "4500 mAh",
                "RAM", "8 GB",
                "Storage", "128 GB",
                "Camera", "50 MP",
                "Display", "6.4 inch",
                "Processor", "Snapdragon 7",
                "Operating System", "Android 14"
        ));

        comparisonData.put("Phone B", Map.of(
                "Price", "$899",
            "Purchase Link", "https://www.flipkart.com/",
                "Battery", "5000 mAh",
                "RAM", "12 GB",
                "Storage", "256 GB",
                "Camera", "108 MP",
                "Display", "6.7 inch",
                "Processor", "Snapdragon 8",
                "Operating System", "Android 15"
        ));

        comparisonData.put("Laptop C", Map.of(
                "Price", "$1299",
            "Purchase Link", "https://www.dell.com/",
                "Battery", "6000 mAh",
                "RAM", "16 GB",
                "Storage", "512 GB",
                "Camera", "N/A",
                "Display", "15.6 inch",
                "Processor", "Intel i7",
                "Operating System", "Windows 11"
        ));

        comparisonData.forEach((productName, valuesByFeature) -> {
            Product product = productsByName.get(productName);
            if (product == null) {
                return;
            }

            valuesByFeature.forEach((featureName, value) -> {
                Feature feature = featuresByName.get(featureName);
                if (feature == null) {
                    return;
                }

                List<FeatureValue> existingValues = featureValueRepository.findByProductIdAndFeatureIdOrderByVersionDesc(
                        product.getId(),
                        feature.getId()
                );

                if (existingValues.isEmpty()) {
                    FeatureValue featureValue = new FeatureValue();
                    featureValue.setProduct(product);
                    featureValue.setFeature(feature);
                    featureValue.setValue(value);
                    featureValue.setVersion(1);
                    featureValue.setUpdatedAt(LocalDateTime.now());
                    featureValueRepository.save(featureValue);
                }
            });
        });
    }
}
