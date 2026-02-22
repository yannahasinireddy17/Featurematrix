package com.productcompare.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "feature_value", indexes = {
    @Index(name = "idx_feature_value_product_feature", columnList = "product_id,feature_id"),
    @Index(name = "idx_feature_value_updated_at", columnList = "updated_at")
})
public class FeatureValue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne
    @JoinColumn(name = "feature_id", nullable = false)
    private Feature feature;

    @Column(nullable = false, columnDefinition = "LONGTEXT")
    private String value;

    @Column(nullable = false)
    private int version;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public FeatureValue() {
    }

    public FeatureValue(Long id, Product product, Feature feature, String value, int version, LocalDateTime updatedAt) {
        this.id = id;
        this.product = product;
        this.feature = feature;
        this.value = value;
        this.version = version;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Feature getFeature() {
        return feature;
    }

    public void setFeature(Feature feature) {
        this.feature = feature;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
