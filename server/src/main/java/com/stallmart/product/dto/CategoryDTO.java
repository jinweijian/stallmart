package com.stallmart.product.dto;

import com.stallmart.store.internal.model.CategoryModule;
import com.stallmart.store.internal.model.CategoryStatus;

public record CategoryDTO(
        Long id,
        Long storeId,
        CategoryModule module,
        String name,
        String iconKey,
        int sortOrder,
        CategoryStatus status
) {
}
