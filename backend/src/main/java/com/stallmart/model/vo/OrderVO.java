package com.stallmart.model.vo;

import lombok.Data;
import lombok.Builder;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class OrderVO {
    private Long id;
    private String orderNo;
    private Long customerId;
    private Long storeId;
    private BigDecimal totalAmount;
    private String status;
    private String confirmCode;
    private String remark;
    private LocalDateTime createdAt;
    private List<OrderItemVO> items;
}
