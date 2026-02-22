package com.productcompare.dto;

public record FeatureValueCellResponse(Long productId, String value, boolean changed, String trend) {
}
