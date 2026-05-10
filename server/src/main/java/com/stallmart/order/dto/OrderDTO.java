package com.stallmart.order.dto;

import com.stallmart.order.internal.model.OrderStatus;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record OrderDTO(
        Long id,
        String orderNo,
        Long userId,
        Long storeId,
        OrderStatus status,
        String confirmCode,
        BigDecimal totalAmount,
        String remark,
        Instant createdAt,
        List<OrderItemDTO> items
) {
}
