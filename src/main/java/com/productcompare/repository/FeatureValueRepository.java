package com.productcompare.repository;

import com.productcompare.entity.FeatureValue;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FeatureValueRepository extends JpaRepository<FeatureValue, Long> {
	List<FeatureValue> findTop2ByProductIdAndFeatureIdOrderByVersionDesc(Long productId, Long featureId);
	List<FeatureValue> findByProductIdAndFeatureIdOrderByVersionDesc(Long productId, Long featureId);
	void deleteByProductId(Long productId);
	void deleteByFeatureId(Long featureId);
	void deleteByProductIdAndFeatureId(Long productId, Long featureId);
}
