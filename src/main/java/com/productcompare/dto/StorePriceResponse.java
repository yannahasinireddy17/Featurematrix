package com.productcompare.dto;

import java.math.BigDecimal;

public class StorePriceResponse {
    private Long id;
    private Long productId;
    private String storeName;
    private BigDecimal price;
    private String buyLink;

    public StorePriceResponse() {
    }

    public StorePriceResponse(Long id, Long productId, String storeName, BigDecimal price, String buyLink) {
        this.id = id;
        this.productId = productId;
        this.storeName = storeName;
        this.price = price;
        this.buyLink = buyLink;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getBuyLink() {
        return buyLink;
    }

    public void setBuyLink(String buyLink) {
        this.buyLink = buyLink;
    }
}
