package com.stallmart.product.dto;

import java.math.BigDecimal;
import java.util.List;

public record ProductSkuDTO(
        Long id,
        List<String> specValues,
        BigDecimal price,
        int stock,
        String status
) {
}
