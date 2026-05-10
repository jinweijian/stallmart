package com.stallmart.product.dto;

import com.stallmart.store.internal.model.CategoryModule;
import com.stallmart.store.internal.model.CategoryStatus;
import jakarta.validation.constraints.NotBlank;

public record CategoryUpsertParams(
        @NotBlank CategoryModule module,
        @NotBlank String name,
        String iconKey,
        int sortOrder,
        CategoryStatus status
) {
}
