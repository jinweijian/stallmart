package com.stallmart.order.dto;

import java.math.BigDecimal;

public record OrderItemDTO(
        Long productId,
        String productName,
        int quantity,
        BigDecimal unitPrice,
        String specsText
) {
}
