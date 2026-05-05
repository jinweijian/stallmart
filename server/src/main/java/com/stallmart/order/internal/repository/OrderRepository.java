package com.stallmart.order.internal.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
    List<OrderEntity> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<OrderEntity> findByStoreIdOrderByCreatedAtDesc(Long storeId);

    List<OrderEntity> findByStoreIdAndUserIdOrderByCreatedAtDesc(Long storeId, Long userId);

    List<OrderEntity> findAllByOrderByCreatedAtDesc();
}
