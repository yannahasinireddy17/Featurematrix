package com.productcompare.repository;

import com.productcompare.entity.StorePrice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StorePriceRepository extends JpaRepository<StorePrice, Long> {
    List<StorePrice> findByProductIdOrderByStoreNameAsc(Long productId);
    Optional<StorePrice> findByProductIdAndStoreNameIgnoreCase(Long productId, String storeName);
    Optional<StorePrice> findByIdAndProductId(Long id, Long productId);
}
