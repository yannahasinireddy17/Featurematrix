package com.productcompare.dto;

public class CompareRecommendationResponse {
    private Long recommendedProductId;
    private String reason;

    public CompareRecommendationResponse() {
    }

    public CompareRecommendationResponse(Long recommendedProductId, String reason) {
        this.recommendedProductId = recommendedProductId;
        this.reason = reason;
    }

    public Long getRecommendedProductId() {
        return recommendedProductId;
    }

    public void setRecommendedProductId(Long recommendedProductId) {
        this.recommendedProductId = recommendedProductId;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
