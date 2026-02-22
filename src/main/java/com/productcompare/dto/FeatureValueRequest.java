package com.productcompare.dto;

public class FeatureValueRequest {
    private String value;

    public FeatureValueRequest() {
    }

    public FeatureValueRequest(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
