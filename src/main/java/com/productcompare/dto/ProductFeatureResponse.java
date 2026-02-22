package com.productcompare.dto;

public class ProductFeatureResponse {
    private String name;
    private String value;
    private String price;

    public ProductFeatureResponse() {
    }

    public ProductFeatureResponse(String name, String value, String price) {
        this.name = name;
        this.value = value;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}