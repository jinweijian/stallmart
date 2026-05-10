package com.stallmart.order.internal.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItemEntity, Long> {
    List<OrderItemEntity> findByOrderIdOrderByIdAsc(Long orderId);

    List<OrderItemEntity> findByOrderIdInOrderByOrderIdAscIdAsc(List<Long> orderIds);
}
