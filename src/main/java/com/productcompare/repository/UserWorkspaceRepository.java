package com.productcompare.repository;

import com.productcompare.entity.UserWorkspace;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserWorkspaceRepository extends JpaRepository<UserWorkspace, Long> {
    Optional<UserWorkspace> findByUsername(String username);
    boolean existsByUsername(String username);
}
