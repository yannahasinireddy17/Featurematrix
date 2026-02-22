package com.productcompare.repository;

import com.productcompare.entity.Feature;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FeatureRepository extends JpaRepository<Feature, Long> {
    List<Feature> findByWorkspaceIdOrderByNameAsc(Long workspaceId);
    Optional<Feature> findByIdAndWorkspaceId(Long id, Long workspaceId);
    Optional<Feature> findByWorkspaceIdAndNameIgnoreCase(Long workspaceId, String name);
}
