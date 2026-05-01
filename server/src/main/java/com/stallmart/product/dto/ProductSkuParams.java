package com.stallmart.product.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

public record ProductSkuParams(
        List<String> specValues,
        @NotNull @DecimalMin("0.00") BigDecimal price,
        int stock,
        String status
) {
}
