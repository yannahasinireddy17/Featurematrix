package com.productcompare.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "product", uniqueConstraints = @UniqueConstraint(columnNames = {"workspace_id", "name"}))
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "category")
    private String category;

    @Column(name = "price")
    private BigDecimal price;

    @Column(name = "image_url", columnDefinition = "LONGTEXT")
    private String imageUrl;

    @ManyToOne
    @JoinColumn(name = "workspace_id", nullable = false)
    private UserWorkspace workspace;

    public Product() {
    }

    public Product(Long id, String name, UserWorkspace workspace) {
        this.id = id;
        this.name = name;
        this.workspace = workspace;
    }

    public Product(String name, String category, BigDecimal price, String imageUrl, UserWorkspace workspace) {
        this.name = name;
        this.category = category;
        this.price = price;
        this.imageUrl = imageUrl;
        this.workspace = workspace;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UserWorkspace getWorkspace() {
        return workspace;
    }

    public void setWorkspace(UserWorkspace workspace) {
        this.workspace = workspace;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
