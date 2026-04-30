package com.stallmart.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class CreateOrderDTO {

    @NotNull(message = "店铺ID不能为空")
    private Long storeId;

    private String remark;

    @NotBlank(message = "商品列表不能为空")
    private List<OrderItemDTO> items;

    @Data
    public static class OrderItemDTO {
        @NotNull(message = "商品ID不能为空")
        private Long productId;

        @NotNull(message = "数量不能为空")
        private Integer quantity;

        private String specName;
    }
}
