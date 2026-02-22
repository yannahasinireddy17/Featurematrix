package com.productcompare.controller;

import com.productcompare.dto.*;
import com.productcompare.entity.UserWorkspace;
import com.productcompare.service.AuthService;
import com.productcompare.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"})
public class ProductController {
    private static final Logger log = LoggerFactory.getLogger(ProductController.class);

    private final ProductService productService;
    private final AuthService authService;

    public ProductController(ProductService productService, AuthService authService) {
        this.productService = productService;
        this.authService = authService;
    }

    @GetMapping("/health")
    public String hello() {
        return "Product Compare Backend is running 🚀";
    }

    @PostMapping("/products")
    public ItemResponse addProduct(@RequestHeader("X-Auth-Token") String token, @RequestBody ProductRequest request) {
        return productService.addProduct(authenticatedWorkspace(token), request);
    }

    @GetMapping("/products")
    public List<?> getAllProducts(@RequestHeader(value = "X-Auth-Token", required = false) String token) {
        boolean publicRequest = token == null || token.isBlank();
        log.info("GET /api/products called (publicRequest={})", publicRequest);
        try {
            if (publicRequest) {
                return productService.getAllProducts();
            }
            return productService.getAllProducts(authenticatedWorkspace(token));
        } catch (Exception exception) {
            log.error("GET /api/products failed (publicRequest={})", publicRequest, exception);
            throw exception;
        }
    }

    @GetMapping("/products/{productId}")
    public ProductDetailsResponse getProductById(
            @RequestHeader("X-Auth-Token") String token,
            @PathVariable Long productId
    ) {
        return productService.getProductById(authenticatedWorkspace(token), productId);
    }

    @PutMapping("/products/{productId}")
    public ItemResponse updateProduct(
            @RequestHeader("X-Auth-Token") String token,
            @PathVariable Long productId,
            @RequestBody ProductRequest request
    ) {
        return productService.updateProduct(authenticatedWorkspace(token), productId, request);
    }

    @DeleteMapping("/products/{productId}")
    public void deleteProduct(@RequestHeader("X-Auth-Token") String token, @PathVariable Long productId) {
        productService.deleteProduct(authenticatedWorkspace(token), productId);
    }

    @PostMapping("/products/{id}/stores")
    public StorePriceResponse addStorePrice(
            @RequestHeader("X-Auth-Token") String token,
            @PathVariable Long id,
            @RequestBody StorePriceRequest request
    ) {
        return productService.addStorePrice(authenticatedWorkspace(token), id, request);
    }

    @GetMapping("/products/{id}/stores")
    public List<StorePriceResponse> getStorePrices(
            @RequestHeader("X-Auth-Token") String token,
            @PathVariable Long id
    ) {
        return productService.getStorePrices(authenticatedWorkspace(token), id);
    }

    @PutMapping("/products/{productId}/stores/{storePriceId}")
    public StorePriceResponse updateStorePrice(
            @RequestHeader("X-Auth-Token") String token,
            @PathVariable Long productId,
            @PathVariable Long storePriceId,
            @RequestBody StorePriceRequest request
    ) {
        return productService.updateStorePrice(authenticatedWorkspace(token), productId, storePriceId, request);
    }

    @DeleteMapping("/products/{productId}/stores/{storePriceId}")
    public void deleteStorePrice(
            @RequestHeader("X-Auth-Token") String token,
            @PathVariable Long productId,
            @PathVariable Long storePriceId
    ) {
        productService.deleteStorePrice(authenticatedWorkspace(token), productId, storePriceId);
    }

    @PostMapping("/features")
    public ItemResponse addFeature(@RequestHeader("X-Auth-Token") String token, @RequestBody NameRequest request) {
        return productService.addFeature(authenticatedWorkspace(token), request);
    }

    @GetMapping("/features")
    public List<ItemResponse> getAllFeatures(@RequestHeader("X-Auth-Token") String token) {
        return productService.getAllFeatures(authenticatedWorkspace(token));
    }

    @PostMapping("/features/defaults")
    public List<ItemResponse> addDefaultFeatures(@RequestHeader("X-Auth-Token") String token) {
        return productService.addDefaultFeatures(authenticatedWorkspace(token));
    }

    @PutMapping("/features/{featureId}")
    public ItemResponse updateFeature(
            @RequestHeader("X-Auth-Token") String token,
            @PathVariable Long featureId,
            @RequestBody NameRequest request
    ) {
        return productService.updateFeature(authenticatedWorkspace(token), featureId, request);
    }

    @DeleteMapping("/features/{featureId}")
    public void deleteFeature(@RequestHeader("X-Auth-Token") String token, @PathVariable Long featureId) {
        productService.deleteFeature(authenticatedWorkspace(token), featureId);
    }

    @PutMapping("/products/{productId}/features/{featureId}/value")
        public FeatureValueResponse updateFeatureValue(
            @RequestHeader("X-Auth-Token") String token,
            @PathVariable Long productId,
            @PathVariable Long featureId,
                @RequestBody(required = false) FeatureValueRequest request
    ) {
        String value = request == null ? null : request.getValue();
        FeatureValueCellResponse updated = productService.updateFeatureValue(
            authenticatedWorkspace(token),
            productId,
            featureId,
            new ValueRequest(value)
        );

        return new FeatureValueResponse(
            updated.productId(),
            featureId,
            updated.value(),
            updated.changed(),
            updated.trend()
        );
    }

    @GetMapping("/products/{productId}/features/{featureId}/history")
    public List<FeatureValueHistoryResponse> getFeatureValueHistory(
            @RequestHeader("X-Auth-Token") String token,
            @PathVariable Long productId,
            @PathVariable Long featureId
    ) {
        return productService.getFeatureValueHistory(authenticatedWorkspace(token), productId, featureId);
    }

    @DeleteMapping("/products/{productId}/features/{featureId}/value")
    public void deleteFeatureValueHistory(
            @RequestHeader("X-Auth-Token") String token,
            @PathVariable Long productId,
            @PathVariable Long featureId
    ) {
        productService.deleteFeatureValueHistory(authenticatedWorkspace(token), productId, featureId);
    }

    @GetMapping("/comparison")
    public ComparisonResponse getComparison(@RequestHeader("X-Auth-Token") String token) {
        return productService.getComparison(authenticatedWorkspace(token));
    }

    @GetMapping("/compare/recommendation")
    public CompareRecommendationResponse getRecommendation(
            @RequestHeader("X-Auth-Token") String token,
            @RequestParam("productA") Long productA,
            @RequestParam("productB") Long productB
    ) {
        return productService.getRecommendation(authenticatedWorkspace(token), productA, productB);
    }

    @GetMapping("/compare")
    public ComparisonResponse compareProducts(
            @RequestParam("p1") Long p1,
            @RequestParam("p2") Long p2
    ) {
        log.info("GET /api/compare called with p1={}, p2={}", p1, p2);
        try {
            return productService.compareProducts(p1, p2);
        } catch (Exception exception) {
            log.error("GET /api/compare failed for p1={}, p2={}", p1, p2, exception);
            throw exception;
        }
    }

    private UserWorkspace authenticatedWorkspace(String token) {
        return authService.getWorkspaceByToken(token);
    }
}
