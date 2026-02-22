package com.productcompare.dto;

public class FeatureValueResponse {
    private Long productId;
    private Long featureId;
    private String value;
    private boolean changed;
    private String trend;

    public FeatureValueResponse() {
    }

    public FeatureValueResponse(Long productId, Long featureId, String value, boolean changed, String trend) {
        this.productId = productId;
        this.featureId = featureId;
        this.value = value;
        this.changed = changed;
        this.trend = trend;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Long getFeatureId() {
        return featureId;
    }

    public void setFeatureId(Long featureId) {
        this.featureId = featureId;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean isChanged() {
        return changed;
    }

    public void setChanged(boolean changed) {
        this.changed = changed;
    }

    public String getTrend() {
        return trend;
    }

    public void setTrend(String trend) {
        this.trend = trend;
    }
}
