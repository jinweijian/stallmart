package com.stallmart.cart.internal.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<CartEntity, Long> {
    List<CartEntity> findByUserIdOrderByUpdatedAtDesc(Long userId);

    List<CartEntity> findByStoreIdOrderByUpdatedAtDesc(Long storeId);

    List<CartEntity> findAllByOrderByUpdatedAtDesc();

    Optional<CartEntity> findByUserIdAndStoreId(Long userId, Long storeId);
}
