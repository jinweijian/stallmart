package com.stallmart.cart.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record AddCartItemParams(
        @NotNull Long storeId,
        @NotNull Long productId,
        @Min(1) int quantity,
        String specsText
) {
}
