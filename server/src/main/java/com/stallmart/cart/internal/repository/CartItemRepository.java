package com.stallmart.cart.internal.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItemEntity, Long> {
    List<CartItemEntity> findByCartIdOrderByIdAsc(Long cartId);

    List<CartItemEntity> findByCartIdInOrderByCartIdAscIdAsc(List<Long> cartIds);

    void deleteByCartId(Long cartId);
}
