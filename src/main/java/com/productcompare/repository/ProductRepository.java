package com.productcompare.repository;

import com.productcompare.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
	List<Product> findByWorkspaceIdOrderByNameAsc(Long workspaceId);
	Optional<Product> findByIdAndWorkspaceId(Long id, Long workspaceId);
}
