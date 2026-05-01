package com.stallmart.product.dto;

import java.math.BigDecimal;

public record ProductDTO(
        Long id,
        Long storeId,
        String name,
        String description,
        BigDecimal price,
        String imageUrl,
        String category,
        String status,
        int sortOrder
) {
}
