package com.productcompare.dto;

import java.util.List;

public record ComparisonResponse(
        List<ItemResponse> products,
        List<ItemResponse> features,
        List<ComparisonRowResponse> rows
) {
}
