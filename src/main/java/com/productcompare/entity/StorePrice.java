package com.productcompare.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(
        name = "store_price",
        uniqueConstraints = @UniqueConstraint(columnNames = {"product_id", "store_name"}),
        indexes = {
                @Index(name = "idx_store_price_product", columnList = "product_id"),
                @Index(name = "idx_store_price_store", columnList = "store_name")
        }
)
public class StorePrice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "product_id", insertable = false, updatable = false)
    private Long productId;

    @Column(name = "store_name", nullable = false)
    private String storeName;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    @Column(name = "buy_link", nullable = false, columnDefinition = "LONGTEXT")
    private String buyLink;

    public StorePrice() {
    }

    public StorePrice(Long id, Product product, String storeName, BigDecimal price, String buyLink) {
        this.id = id;
        this.product = product;
        this.storeName = storeName;
        this.price = price;
        this.buyLink = buyLink;
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

    public Long getProductId() {
        return productId;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getBuyLink() {
        return buyLink;
    }

    public void setBuyLink(String buyLink) {
        this.buyLink = buyLink;
    }
}
