package com.stallmart.product.dto;

import jakarta.validation.constraints.NotBlank;

public record CategoryUpsertParams(
        @NotBlank String module,
        @NotBlank String name,
        int sortOrder,
        String status
) {
}
