package com.stallmart.cart.dto;

import com.stallmart.cart.internal.model.CartStatus;
import java.time.Instant;
import java.util.List;

public record CartDTO(
        Long id,
        Long userId,
        Long storeId,
        CartStatus status,
        Instant updatedAt,
        List<CartItemDTO> items
) {
}
