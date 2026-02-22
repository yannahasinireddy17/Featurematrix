package com.productcompare.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ProductDetailsResponse {
    private Long id;
    private String name;
    private String category;
    private BigDecimal price;
    private String imageUrl;
    private String buyLink;
    private List<ProductFeatureResponse> features = new ArrayList<>();

    public ProductDetailsResponse() {
    }

    public ProductDetailsResponse(Long id, String name, String category, BigDecimal price, String imageUrl, String buyLink, List<ProductFeatureResponse> features) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.price = price;
        this.imageUrl = imageUrl;
        this.buyLink = buyLink;
        this.features = features;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getBuyLink() {
        return buyLink;
    }

    public void setBuyLink(String buyLink) {
        this.buyLink = buyLink;
    }

    public List<ProductFeatureResponse> getFeatures() {
        return features;
    }

    public void setFeatures(List<ProductFeatureResponse> features) {
        this.features = features;
    }
}