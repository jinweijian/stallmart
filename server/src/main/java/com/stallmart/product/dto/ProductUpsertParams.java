package com.stallmart.product.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record ProductUpsertParams(
        @NotBlank String name,
        String description,
        @NotNull @DecimalMin("0.00") BigDecimal price,
        String imageUrl,
        String category,
        String status,
        int sortOrder
) {
}
