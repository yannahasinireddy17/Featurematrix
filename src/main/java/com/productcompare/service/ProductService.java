package com.productcompare.service;

import com.productcompare.dto.*;
import com.productcompare.entity.Feature;
import com.productcompare.entity.FeatureValue;
import com.productcompare.entity.Product;
import com.productcompare.entity.UserWorkspace;
import com.productcompare.exception.DuplicateStoreException;
import com.productcompare.repository.FeatureRepository;
import com.productcompare.repository.FeatureValueRepository;
import com.productcompare.repository.ProductRepository;
import com.productcompare.repository.StorePriceRepository;
import org.springframework.data.domain.Sort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class ProductService {
    private static final Logger log = LoggerFactory.getLogger(ProductService.class);

    private static final List<String> DEFAULT_FEATURES = List.of(
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

    private final ProductRepository productRepository;
    private final FeatureRepository featureRepository;
    private final FeatureValueRepository featureValueRepository;
    private final StorePriceRepository storePriceRepository;

    public ProductService(
            ProductRepository productRepository,
            FeatureRepository featureRepository,
            FeatureValueRepository featureValueRepository,
            StorePriceRepository storePriceRepository
    ) {
        this.productRepository = productRepository;
        this.featureRepository = featureRepository;
        this.featureValueRepository = featureValueRepository;
        this.storePriceRepository = storePriceRepository;
    }

    @Transactional
    public StorePriceResponse addStorePrice(UserWorkspace workspace, Long productId, StorePriceRequest request) {
        Product product = getProduct(workspace.getId(), productId);
        validateName(request.getStoreName(), "Store name");

        if (request.getPrice() == null) {
            throw new ResponseStatusException(BAD_REQUEST, "Price is required");
        }

        validateName(request.getBuyLink(), "Buy link");
        if (toUriOrNull(request.getBuyLink().trim()) == null) {
            throw new ResponseStatusException(BAD_REQUEST, "Buy link must be a valid URL");
        }

        com.productcompare.entity.StorePrice storePrice = storePriceRepository
                .findByProductIdAndStoreNameIgnoreCase(product.getId(), request.getStoreName().trim())
                .orElseGet(com.productcompare.entity.StorePrice::new);

        if (storePrice.getId() != null) {
            throw new DuplicateStoreException("Store already exists for this product");
        }

        storePrice.setProduct(product);
        storePrice.setStoreName(request.getStoreName().trim());
        storePrice.setPrice(request.getPrice());
        storePrice.setBuyLink(request.getBuyLink().trim());

        com.productcompare.entity.StorePrice saved = storePriceRepository.save(storePrice);
        return toStorePriceResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<StorePriceResponse> getStorePrices(UserWorkspace workspace, Long productId) {
        Product product = getProduct(workspace.getId(), productId);
        return storePriceRepository.findByProductIdOrderByStoreNameAsc(product.getId())
                .stream()
                .map(this::toStorePriceResponse)
                .toList();
    }

    @Transactional
    public StorePriceResponse updateStorePrice(UserWorkspace workspace, Long productId, Long storePriceId, StorePriceRequest request) {
        Product product = getProduct(workspace.getId(), productId);
        validateName(request.getStoreName(), "Store name");

        if (request.getPrice() == null) {
            throw new ResponseStatusException(BAD_REQUEST, "Price is required");
        }

        validateName(request.getBuyLink(), "Buy link");
        if (toUriOrNull(request.getBuyLink().trim()) == null) {
            throw new ResponseStatusException(BAD_REQUEST, "Buy link must be a valid URL");
        }

        com.productcompare.entity.StorePrice storePrice = getStorePrice(product.getId(), storePriceId);
        if (!product.getId().equals(storePrice.getProductId())) {
            throw new ResponseStatusException(BAD_REQUEST, "productId does not match store price productId");
        }

        storePriceRepository.findByProductIdAndStoreNameIgnoreCase(product.getId(), request.getStoreName().trim())
                .ifPresent(existing -> {
                    if (!existing.getId().equals(storePriceId)) {
                        throw new DuplicateStoreException("Store already exists for this product");
                    }
                });

        storePrice.setStoreName(request.getStoreName().trim());
        storePrice.setPrice(request.getPrice());
        storePrice.setBuyLink(request.getBuyLink().trim());

        com.productcompare.entity.StorePrice saved = storePriceRepository.save(storePrice);
        return toStorePriceResponse(saved);
    }

    @Transactional
    public void deleteStorePrice(UserWorkspace workspace, Long productId, Long storePriceId) {
        Product product = getProduct(workspace.getId(), productId);
        com.productcompare.entity.StorePrice storePrice = getStorePrice(product.getId(), storePriceId);
        if (!product.getId().equals(storePrice.getProductId())) {
            throw new ResponseStatusException(BAD_REQUEST, "productId does not match store price productId");
        }
        storePriceRepository.delete(storePrice);
    }

    @Transactional
    public ItemResponse addProduct(UserWorkspace workspace, ProductRequest request) {
        validateName(request.getName(), "Product name");
        Product product = new Product();
        product.setName(request.getName().trim());
        product.setCategory(request.getCategory() != null ? request.getCategory().trim() : null);
        product.setPrice(request.getPrice());
        product.setImageUrl(request.getImageUrl() != null ? request.getImageUrl().trim() : null);
        product.setWorkspace(workspace);
        Product saved = productRepository.save(product);

        saveProductFeatures(workspace, saved, request);

        return new ItemResponse(saved.getId(), saved.getName());
    }

    @Transactional(readOnly = true)
    public ProductDetailsResponse getProductById(UserWorkspace workspace, Long productId) {
        Product product = getProduct(workspace.getId(), productId);
        List<Feature> workspaceFeatures = featureRepository.findByWorkspaceIdOrderByNameAsc(workspace.getId());

        String buyLink = null;
        List<ProductFeatureResponse> productFeatures = new ArrayList<>();

        for (Feature feature : workspaceFeatures) {
            List<FeatureValue> latest = featureValueRepository
                    .findTop2ByProductIdAndFeatureIdOrderByVersionDesc(product.getId(), feature.getId());

            if (latest.isEmpty()) {
                continue;
            }

            String storedValue = latest.get(0).getValue();
            if (isLinkFeature(feature.getName())) {
                buyLink = storedValue;
                continue;
            }

            ProductFeatureResponse parsed = toFeatureResponse(feature.getName(), storedValue);
            productFeatures.add(parsed);
        }

        return new ProductDetailsResponse(
                product.getId(),
                product.getName(),
                product.getCategory(),
                product.getPrice(),
                product.getImageUrl(),
                buyLink,
                productFeatures
        );
    }

    @Transactional(readOnly = true)
    public List<ItemResponse> getAllProducts(UserWorkspace workspace) {
        return productRepository.findByWorkspaceIdOrderByNameAsc(workspace.getId())
                .stream()
                .map(item -> new ItemResponse(item.getId(), item.getName()))
                .toList();
    }

        @Transactional(readOnly = true)
        public List<ProductResponse> getAllProducts() {
            try {
                List<Product> products = productRepository.findAll(Sort.by(Sort.Direction.ASC, "name"));
                if (products == null || products.isEmpty()) {
                    log.info("Public products query returned no rows");
                    return List.of();
                }

                return products.stream()
                        .map(product -> new ProductResponse(
                                product.getId(),
                                product.getName(),
                                product.getCategory(),
                                product.getPrice(),
                                product.getImageUrl()
                        ))
                        .toList();
            } catch (Exception exception) {
                log.error("Failed to fetch products from database", exception);
                throw exception;
            }
        }

    @Transactional
    public ItemResponse updateProduct(UserWorkspace workspace, Long productId, ProductRequest request) {
        validateName(request.getName(), "Product name");
        Product product = getProduct(workspace.getId(), productId);
        product.setName(request.getName().trim());
        product.setCategory(request.getCategory() != null ? request.getCategory().trim() : null);
        product.setPrice(request.getPrice());
        product.setImageUrl(request.getImageUrl() != null ? request.getImageUrl().trim() : null);
        Product saved = productRepository.save(product);

        saveProductFeatures(workspace, saved, request);

        return new ItemResponse(saved.getId(), saved.getName());
    }

    @Transactional
    public void deleteProduct(UserWorkspace workspace, Long productId) {
        Product product = getProduct(workspace.getId(), productId);
        featureValueRepository.deleteByProductId(product.getId());
        productRepository.delete(product);
    }

    @Transactional
    public ItemResponse addFeature(UserWorkspace workspace, NameRequest request) {
        validateName(request.name(), "Feature name");
        Feature feature = new Feature();
        feature.setName(request.name().trim());
        feature.setImportance(1);
        feature.setWorkspace(workspace);
        Feature saved = featureRepository.save(feature);
        return new ItemResponse(saved.getId(), saved.getName());
    }

    @Transactional
    public List<ItemResponse> addDefaultFeatures(UserWorkspace workspace) {
        List<Feature> existing = featureRepository.findByWorkspaceIdOrderByNameAsc(workspace.getId());
        Set<String> existingNames = existing.stream()
                .map(item -> item.getName().trim().toLowerCase())
                .collect(java.util.stream.Collectors.toSet());

        for (String defaultFeature : DEFAULT_FEATURES) {
            if (!existingNames.contains(defaultFeature.toLowerCase())) {
                Feature feature = new Feature();
                feature.setName(defaultFeature);
                feature.setImportance(1);
                feature.setWorkspace(workspace);
                featureRepository.save(feature);
            }
        }

        return featureRepository.findByWorkspaceIdOrderByNameAsc(workspace.getId())
                .stream()
                .map(item -> new ItemResponse(item.getId(), item.getName()))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ItemResponse> getAllFeatures(UserWorkspace workspace) {
        return featureRepository.findByWorkspaceIdOrderByNameAsc(workspace.getId())
                .stream()
                .map(item -> new ItemResponse(item.getId(), item.getName()))
                .toList();
    }

    @Transactional
    public ItemResponse updateFeature(UserWorkspace workspace, Long featureId, NameRequest request) {
        validateName(request.name(), "Feature name");
        Feature feature = getFeature(workspace.getId(), featureId);
        feature.setName(request.name().trim());
        Feature saved = featureRepository.save(feature);
        return new ItemResponse(saved.getId(), saved.getName());
    }

    @Transactional
    public void deleteFeature(UserWorkspace workspace, Long featureId) {
        Feature feature = getFeature(workspace.getId(), featureId);
        featureValueRepository.deleteByFeatureId(feature.getId());
        featureRepository.delete(feature);
    }

    @Transactional
    public FeatureValueCellResponse updateFeatureValue(UserWorkspace workspace, Long productId, Long featureId, ValueRequest request) {
        String requestedValue = request == null ? null : request.value();
        validateName(requestedValue, "Feature value");
        Product product = getProduct(workspace.getId(), productId);
        Feature feature = getFeature(workspace.getId(), featureId);
        if (!feature.getWorkspace().getId().equals(product.getWorkspace().getId())) {
            throw new ResponseStatusException(NOT_FOUND, "Feature not found for product");
        }

        String normalizedValue = requestedValue.trim();
        validateFeatureValue(feature.getName(), normalizedValue);

        List<FeatureValue> recent = featureValueRepository
                .findTop2ByProductIdAndFeatureIdOrderByVersionDesc(product.getId(), feature.getId());

        int nextVersion = recent.isEmpty() ? 1 : recent.get(0).getVersion() + 1;
        String previousValue = recent.isEmpty() ? null : recent.get(0).getValue();

        FeatureValue current = new FeatureValue();
        current.setProduct(product);
        current.setFeature(feature);
        current.setValue(normalizedValue);
        current.setVersion(nextVersion);
        current.setUpdatedAt(LocalDateTime.now());
        featureValueRepository.save(current);

        String trend = resolveTrend(previousValue, current.getValue());
        return new FeatureValueCellResponse(product.getId(), current.getValue(), previousValue != null && !previousValue.equals(current.getValue()), trend);
    }

    @Transactional(readOnly = true)
    public List<FeatureValueHistoryResponse> getFeatureValueHistory(UserWorkspace workspace, Long productId, Long featureId) {
        Product product = getProduct(workspace.getId(), productId);
        Feature feature = getFeature(workspace.getId(), featureId);

        List<FeatureValue> values = featureValueRepository
                .findByProductIdAndFeatureIdOrderByVersionDesc(product.getId(), feature.getId());

        List<FeatureValueHistoryResponse> history = new ArrayList<>();
        String previous = null;
        for (FeatureValue value : values) {
            String trend = resolveTrend(previous, value.getValue());
            boolean changed = previous != null && !previous.equals(value.getValue());
            history.add(new FeatureValueHistoryResponse(
                    product.getId(),
                    feature.getId(),
                    value.getVersion(),
                    value.getValue(),
                    changed,
                    trend,
                    value.getUpdatedAt()
            ));
            previous = value.getValue();
        }
        return history;
    }

    @Transactional
    public void deleteFeatureValueHistory(UserWorkspace workspace, Long productId, Long featureId) {
        Product product = getProduct(workspace.getId(), productId);
        Feature feature = getFeature(workspace.getId(), featureId);
        featureValueRepository.deleteByProductIdAndFeatureId(product.getId(), feature.getId());
    }

    @Transactional(readOnly = true)
    public ComparisonResponse getComparison(UserWorkspace workspace) {
        List<Product> products = productRepository.findByWorkspaceIdOrderByNameAsc(workspace.getId());
        List<Feature> features = featureRepository.findByWorkspaceIdOrderByNameAsc(workspace.getId());

        List<ItemResponse> productItems = products.stream().map(p -> new ItemResponse(p.getId(), p.getName())).toList();
        List<ItemResponse> featureItems = features.stream().map(f -> new ItemResponse(f.getId(), f.getName())).toList();

        List<ComparisonRowResponse> rows = new ArrayList<>();
        Map<Long, Map<Long, FeatureValueCellResponse>> latestCellMap = new HashMap<>();

        for (Product product : products) {
            Map<Long, FeatureValueCellResponse> featureCells = new HashMap<>();
            for (Feature feature : features) {
                List<FeatureValue> latest = featureValueRepository
                        .findTop2ByProductIdAndFeatureIdOrderByVersionDesc(product.getId(), feature.getId());
                if (!latest.isEmpty()) {
                    String previous = latest.size() > 1 ? latest.get(1).getValue() : null;
                    FeatureValue current = latest.get(0);
                    featureCells.put(feature.getId(), new FeatureValueCellResponse(
                            product.getId(),
                            current.getValue(),
                            previous != null && !previous.equals(current.getValue()),
                            resolveTrend(previous, current.getValue())
                    ));
                }
            }
            latestCellMap.put(product.getId(), featureCells);
        }

        for (Feature feature : features) {
            List<FeatureValueCellResponse> cells = new ArrayList<>();
            for (Product product : products) {
                FeatureValueCellResponse cell = latestCellMap.getOrDefault(product.getId(), Map.of()).get(feature.getId());
                if (cell == null) {
                    cells.add(new FeatureValueCellResponse(product.getId(), "-", false, "same"));
                } else {
                    cells.add(cell);
                }
            }
            rows.add(new ComparisonRowResponse(feature.getId(), feature.getName(), cells));
        }

        return new ComparisonResponse(productItems, featureItems, rows);
    }

    @Transactional(readOnly = true)
    public CompareRecommendationResponse getRecommendation(UserWorkspace workspace, Long productA, Long productB) {
        if (productA == null || productB == null) {
            throw new ResponseStatusException(BAD_REQUEST, "Both productA and productB are required");
        }
        if (productA.equals(productB)) {
            throw new ResponseStatusException(BAD_REQUEST, "productA and productB must be different");
        }

        Product left = getProduct(workspace.getId(), productA);
        Product right = getProduct(workspace.getId(), productB);

        int leftPoints = 0;
        int rightPoints = 0;
        List<String> leftReasons = new ArrayList<>();
        List<String> rightReasons = new ArrayList<>();

        StoreMetric leftStorePrice = getBestStorePrice(left.getId());
        StoreMetric rightStorePrice = getBestStorePrice(right.getId());

        if (leftStorePrice.price != null && rightStorePrice.price != null) {
            if (leftStorePrice.price < rightStorePrice.price) {
                leftPoints++;
                leftReasons.add("Lower price" + (leftStorePrice.storeName == null ? "" : " on " + leftStorePrice.storeName));
            } else if (rightStorePrice.price < leftStorePrice.price) {
                rightPoints++;
                rightReasons.add("Lower price" + (rightStorePrice.storeName == null ? "" : " on " + rightStorePrice.storeName));
            }
        }

        Double leftRam = getNumericFeatureValue(workspace, left.getId(), "ram");
        Double rightRam = getNumericFeatureValue(workspace, right.getId(), "ram");
        if (leftRam != null && rightRam != null) {
            if (leftRam > rightRam) {
                leftPoints++;
                leftReasons.add("Better RAM configuration");
            } else if (rightRam > leftRam) {
                rightPoints++;
                rightReasons.add("Better RAM configuration");
            }
        }

        Double leftBattery = getNumericFeatureValue(workspace, left.getId(), "battery");
        Double rightBattery = getNumericFeatureValue(workspace, right.getId(), "battery");
        if (leftBattery != null && rightBattery != null) {
            if (leftBattery > rightBattery) {
                leftPoints++;
                leftReasons.add("Higher battery capacity");
            } else if (rightBattery > leftBattery) {
                rightPoints++;
                rightReasons.add("Higher battery capacity");
            }
        }

        Product winner;
        List<String> winnerReasons;
        if (rightPoints > leftPoints) {
            winner = right;
            winnerReasons = rightReasons;
        } else {
            winner = left;
            winnerReasons = leftReasons;
        }

        if (winnerReasons.isEmpty()) {
            winnerReasons = List.of("Balanced overall value across compared specs");
        }

        String reason = String.join(" and ", winnerReasons.stream().limit(2).toList());
        return new CompareRecommendationResponse(winner.getId(), reason);
    }

    @Transactional(readOnly = true)
    public ComparisonResponse compareProducts(Long p1, Long p2) {
        log.info("Public compare request received: p1={}, p2={}", p1, p2);

        if (p1 == null || p2 == null) {
            throw new ResponseStatusException(BAD_REQUEST, "Both p1 and p2 are required");
        }
        if (p1.equals(p2)) {
            throw new ResponseStatusException(BAD_REQUEST, "p1 and p2 must be different");
        }

        try {
            Product product1 = productRepository.findById(p1)
                    .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Product not found: " + p1));
            Product product2 = productRepository.findById(p2)
                    .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Product not found: " + p2));

            if (product1.getWorkspace() == null || product2.getWorkspace() == null) {
                throw new ResponseStatusException(BAD_REQUEST, "Products must belong to a valid workspace");
            }

            List<Product> products = List.of(product1, product2);
            List<ItemResponse> productItems = products.stream()
                    .map(product -> new ItemResponse(product.getId(), product.getName()))
                    .toList();

            Map<Long, List<Feature>> featuresByWorkspace = new HashMap<>();
            featuresByWorkspace.put(product1.getWorkspace().getId(), featureRepository.findByWorkspaceIdOrderByNameAsc(product1.getWorkspace().getId()));
            if (!product1.getWorkspace().getId().equals(product2.getWorkspace().getId())) {
                featuresByWorkspace.put(product2.getWorkspace().getId(), featureRepository.findByWorkspaceIdOrderByNameAsc(product2.getWorkspace().getId()));
            }

            Map<String, ItemResponse> featureItemsByName = new LinkedHashMap<>();
            for (List<Feature> workspaceFeatures : featuresByWorkspace.values()) {
                if (workspaceFeatures == null || workspaceFeatures.isEmpty()) {
                    continue;
                }
                for (Feature feature : workspaceFeatures) {
                    if (feature == null || feature.getName() == null || feature.getName().isBlank()) {
                        continue;
                    }
                    String normalizedName = feature.getName().trim().toLowerCase();
                    featureItemsByName.putIfAbsent(normalizedName, new ItemResponse(feature.getId(), feature.getName()));
                }
            }

            List<ItemResponse> featureItems = featureItemsByName.values().stream()
                    .sorted(Comparator.comparing(ItemResponse::name, String.CASE_INSENSITIVE_ORDER))
                    .toList();

            List<ComparisonRowResponse> rows = new ArrayList<>();
            for (ItemResponse featureItem : featureItems) {
                List<FeatureValueCellResponse> cells = new ArrayList<>();

                for (Product product : products) {
                    List<Feature> workspaceFeatures = featuresByWorkspace.getOrDefault(product.getWorkspace().getId(), List.of());
                    Feature matchingFeature = workspaceFeatures.stream()
                            .filter(feature -> feature.getName() != null && feature.getName().equalsIgnoreCase(featureItem.name()))
                            .findFirst()
                            .orElse(null);

                    if (matchingFeature == null) {
                        cells.add(new FeatureValueCellResponse(product.getId(), "-", false, "same"));
                        continue;
                    }

                    List<FeatureValue> latest = featureValueRepository
                            .findTop2ByProductIdAndFeatureIdOrderByVersionDesc(product.getId(), matchingFeature.getId());
                    if (latest == null || latest.isEmpty()) {
                        cells.add(new FeatureValueCellResponse(product.getId(), "-", false, "same"));
                        continue;
                    }

                    String previous = latest.size() > 1 ? latest.get(1).getValue() : null;
                    String current = latest.get(0).getValue();
                    cells.add(new FeatureValueCellResponse(
                            product.getId(),
                            current == null || current.isBlank() ? "-" : current,
                            previous != null && !previous.equals(current),
                            resolveTrend(previous, current)
                    ));
                }

                rows.add(new ComparisonRowResponse(featureItem.id(), featureItem.name(), cells));
            }

            return new ComparisonResponse(productItems, featureItems, rows);
        } catch (ResponseStatusException exception) {
            log.warn("Compare validation failed for p1={}, p2={}: {}", p1, p2, exception.getReason());
            throw exception;
        } catch (Exception exception) {
            log.error("Unexpected error while building comparison for p1={}, p2={}", p1, p2, exception);
            throw exception;
        }
    }

    private Product getProduct(Long workspaceId, Long productId) {
        return productRepository.findByIdAndWorkspaceId(productId, workspaceId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Product not found"));
    }

    private Double getNumericFeatureValue(UserWorkspace workspace, Long productId, String featureKeyword) {
        List<Feature> allFeatures = featureRepository.findByWorkspaceIdOrderByNameAsc(workspace.getId());
        for (Feature feature : allFeatures) {
            if (!feature.getName().toLowerCase().contains(featureKeyword.toLowerCase())) {
                continue;
            }

            List<FeatureValue> values = featureValueRepository
                    .findTop2ByProductIdAndFeatureIdOrderByVersionDesc(productId, feature.getId());
            if (values.isEmpty()) {
                continue;
            }

            Double number = extractNumber(values.get(0).getValue());
            if (number != null) {
                return number;
            }
        }

        return null;
    }

    private StoreMetric getBestStorePrice(Long productId) {
        List<com.productcompare.entity.StorePrice> stores = storePriceRepository.findByProductIdOrderByStoreNameAsc(productId);
        double bestPrice = Double.MAX_VALUE;
        String bestStore = null;

        for (com.productcompare.entity.StorePrice store : stores) {
            if (store.getPrice() == null) {
                continue;
            }
            double value = store.getPrice().doubleValue();
            if (value < bestPrice) {
                bestPrice = value;
                bestStore = store.getStoreName();
            }
        }

        if (bestStore == null) {
            return new StoreMetric(null, null);
        }
        return new StoreMetric(bestPrice, bestStore);
    }

    private Double extractNumber(String rawValue) {
        if (rawValue == null) {
            return null;
        }
        String cleaned = rawValue.replaceAll("[^0-9.]", " ").trim();
        if (cleaned.isEmpty()) {
            return null;
        }
        String firstToken = cleaned.split("\\\\s+")[0];
        try {
            return Double.parseDouble(firstToken);
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

    private static class StoreMetric {
        private final Double price;
        private final String storeName;

        private StoreMetric(Double price, String storeName) {
            this.price = price;
            this.storeName = storeName;
        }
    }

    private com.productcompare.entity.StorePrice getStorePrice(Long productId, Long storePriceId) {
        return storePriceRepository.findByIdAndProductId(storePriceId, productId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Store price not found"));
    }

    private void saveProductFeatures(UserWorkspace workspace, Product saved, ProductRequest request) {
        if (request.getBuyLink() != null && !request.getBuyLink().trim().isEmpty()) {
            Feature purchaseLinkFeature = findOrCreateFeature(workspace, "Purchase Link");
            updateFeatureValue(
                    workspace,
                    saved.getId(),
                    purchaseLinkFeature.getId(),
                    new ValueRequest(request.getBuyLink().trim())
            );
        }

        if (request.getFeatures() == null || request.getFeatures().isEmpty()) {
            return;
        }

        for (ProductFeatureRequest featureRequest : request.getFeatures()) {
            if (featureRequest == null) {
                continue;
            }

            String featureName = featureRequest.getName() == null ? "" : featureRequest.getName().trim();
            String featureValue = featureRequest.getValue() == null ? "" : featureRequest.getValue().trim();
            String featurePrice = featureRequest.getPrice() == null ? "" : featureRequest.getPrice().trim();

            if (featureName.isEmpty() || featureValue.isEmpty()) {
                continue;
            }

            Feature feature = findOrCreateFeature(workspace, featureName);
            String encodedValue = encodeFeatureValue(featureValue, featurePrice);

            updateFeatureValue(
                    workspace,
                    saved.getId(),
                    feature.getId(),
                    new ValueRequest(encodedValue)
            );
        }
    }

    private Feature findOrCreateFeature(UserWorkspace workspace, String name) {
        return featureRepository
                .findByWorkspaceIdAndNameIgnoreCase(workspace.getId(), name)
                .orElseGet(() -> {
                    Feature feature = new Feature();
                    feature.setName(name.trim());
                    feature.setImportance(1);
                    feature.setWorkspace(workspace);
                    return featureRepository.save(feature);
                });
    }

    private String encodeFeatureValue(String value, String price) {
        String normalizedValue = value == null ? "" : value.trim();
        String normalizedPrice = price == null ? "" : price.trim();
        if (normalizedPrice.isEmpty()) {
            return normalizedValue;
        }
        return normalizedValue + " ||price|| " + normalizedPrice;
    }

    private ProductFeatureResponse toFeatureResponse(String featureName, String storedValue) {
        if (storedValue == null) {
            return new ProductFeatureResponse(featureName, "", null);
        }

        String[] parts = storedValue.split(" \\\\|\\\\|price\\\\|\\\\| ", 2);
        if (parts.length == 2) {
            return new ProductFeatureResponse(featureName, parts[0], parts[1]);
        }

        return new ProductFeatureResponse(featureName, storedValue, null);
    }

    private Feature getFeature(Long workspaceId, Long featureId) {
        return featureRepository.findByIdAndWorkspaceId(featureId, workspaceId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Feature not found"));
    }

    private void validateName(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new ResponseStatusException(BAD_REQUEST, fieldName + " is required");
        }
    }

    private void validateFeatureValue(String featureName, String value) {
        if (!isLinkFeature(featureName)) {
            return;
        }

        if (toUriOrNull(value) != null) {
            return;
        }

        if (value.startsWith("www.") && toUriOrNull("https://" + value) != null) {
            return;
        }

        throw new ResponseStatusException(BAD_REQUEST, "Feature value must be a valid URL for link features");
    }

    private boolean isLinkFeature(String featureName) {
        String normalized = featureName == null ? "" : featureName.trim().toLowerCase();
        return normalized.contains("link")
                || normalized.contains("url")
                || normalized.contains("buy")
                || normalized.contains("purchase")
                || normalized.contains("store");
    }

    private URI toUriOrNull(String value) {
        try {
            URI uri = new URI(value);
            String scheme = uri.getScheme();
            String host = uri.getHost();
            if (("http".equalsIgnoreCase(scheme) || "https".equalsIgnoreCase(scheme)) && host != null && !host.isBlank()) {
                return uri;
            }
            return null;
        } catch (URISyntaxException ignored) {
            return null;
        }
    }

    private String resolveTrend(String oldValue, String newValue) {
        if (oldValue == null || oldValue.equals(newValue)) {
            return "same";
        }
        return newValue.compareToIgnoreCase(oldValue) > 0 ? "up" : "down";
    }

    private StorePriceResponse toStorePriceResponse(com.productcompare.entity.StorePrice storePrice) {
        return new StorePriceResponse(
                storePrice.getId(),
                storePrice.getProductId(),
                storePrice.getStoreName(),
                storePrice.getPrice(),
                storePrice.getBuyLink()
        );
    }

}
