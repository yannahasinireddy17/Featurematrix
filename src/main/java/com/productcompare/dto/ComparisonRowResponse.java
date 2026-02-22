package com.productcompare.dto;

import java.util.List;

public record ComparisonRowResponse(Long featureId, String featureName, List<FeatureValueCellResponse> cells) {
}
