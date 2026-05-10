package com.stallmart.product.dto;

import com.stallmart.store.internal.model.ProductStatus;
import java.math.BigDecimal;
import java.util.List;

public record ProductDTO(
        Long id,
        Long storeId,
        Long categoryId,
        String categoryName,
        String name,
        String description,
        BigDecimal price,
        String imageUrl,
        String mainImageUrl,
        String category,
        ProductStatus status,
        int sortOrder,
        List<Long> specIds,
        List<ProductSkuDTO> skus
) {
}
