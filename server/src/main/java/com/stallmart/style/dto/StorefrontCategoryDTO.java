package com.stallmart.style.dto;

public record StorefrontCategoryDTO(
        String id,
        String name,
        String iconKey,
        String iconUrl,
        String fallbackText,
        int sortOrder,
        String status
) {
}
