package com.stallmart.cart.dto;

import java.math.BigDecimal;

public record CartItemDTO(
        Long productId,
        String productName,
        int quantity,
        BigDecimal unitPrice,
        String specsText
) {
}
