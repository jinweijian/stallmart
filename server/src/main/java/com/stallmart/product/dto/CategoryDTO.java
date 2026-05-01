package com.stallmart.product.dto;

public record CategoryDTO(
        Long id,
        Long storeId,
        String module,
        String name,
        int sortOrder,
        String status
) {
}
