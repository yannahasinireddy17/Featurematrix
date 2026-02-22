package com.productcompare.dto;

import java.math.BigDecimal;

public class StorePriceRequest {
    private String storeName;
    private BigDecimal price;
    private String buyLink;

    public StorePriceRequest() {
    }

    public StorePriceRequest(String storeName, BigDecimal price, String buyLink) {
        this.storeName = storeName;
        this.price = price;
        this.buyLink = buyLink;
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
