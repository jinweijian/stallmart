package com.stallmart.order.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record OrderDTO(
        Long id,
        String orderNo,
        Long userId,
        Long storeId,
        String status,
        String confirmCode,
        BigDecimal totalAmount,
        String remark,
        Instant createdAt,
        List<OrderItemDTO> items
) {
}
