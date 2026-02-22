package com.productcompare.dto;

import java.math.BigDecimal;

public class ProductRequest {
    private String name;
    private String category;
    private BigDecimal price;
    private String imageUrl;
    private String buyLink;
    private java.util.List<ProductFeatureRequest> features;

    public ProductRequest() {
    }

    public ProductRequest(String name, String category, BigDecimal price, String imageUrl, String buyLink, java.util.List<ProductFeatureRequest> features) {
        this.name = name;
        this.category = category;
        this.price = price;
        this.imageUrl = imageUrl;
        this.buyLink = buyLink;
        this.features = features;
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

    public java.util.List<ProductFeatureRequest> getFeatures() {
        return features;
    }

    public void setFeatures(java.util.List<ProductFeatureRequest> features) {
        this.features = features;
    }
}
