package com.stallmart.product.dto;

import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.util.List;

public record ProductUpsertParams(
        @NotBlank String name,
        String description,
        BigDecimal price,
        Long categoryId,
        String imageUrl,
        String mainImageUrl,
        String category,
        String status,
        int sortOrder,
        List<Long> specIds,
        List<ProductSkuParams> skus
) {
}
