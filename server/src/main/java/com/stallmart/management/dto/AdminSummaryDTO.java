package com.stallmart.management.dto;

import java.math.BigDecimal;

public record AdminSummaryDTO(
        long storeCount,
        long userCount,
        long orderCount,
        long cartCount,
        BigDecimal salesAmount
) {
}
