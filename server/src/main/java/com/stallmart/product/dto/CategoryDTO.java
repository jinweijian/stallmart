package com.stallmart.product.dto;

public record CategoryDTO(
        Long id,
        Long storeId,
        String module,
        String name,
        String iconKey,
        int sortOrder,
        String status
) {
}
