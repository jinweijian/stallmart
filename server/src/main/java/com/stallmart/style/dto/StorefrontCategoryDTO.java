package com.stallmart.style.dto;

import com.stallmart.store.internal.model.CategoryStatus;

public record StorefrontCategoryDTO(
        String id,
        String name,
        String iconKey,
        String iconUrl,
        String fallbackText,
        int sortOrder,
        CategoryStatus status
) {
}
