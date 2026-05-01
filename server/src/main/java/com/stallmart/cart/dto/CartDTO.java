package com.stallmart.cart.dto;

import java.time.Instant;
import java.util.List;

public record CartDTO(
        Long id,
        Long userId,
        Long storeId,
        String status,
        Instant updatedAt,
        List<CartItemDTO> items
) {
}
