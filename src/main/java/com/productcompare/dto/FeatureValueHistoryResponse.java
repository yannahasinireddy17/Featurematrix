package com.productcompare.dto;

import java.time.LocalDateTime;

public record FeatureValueHistoryResponse(
        Long productId,
        Long featureId,
        int version,
        String value,
        boolean changed,
        String trend,
        LocalDateTime updatedAt
) {
}
