package com.stallmart.model.vo;

import lombok.Data;
import lombok.Builder;
import java.math.BigDecimal;

/**
 * 商品信息 VO
 */
@Data
@Builder
public class ProductVO {
    private Long id;
    private Long storeId;
    private String name;
    private String description;
    private BigDecimal price;
    private String imageUrl;
    private String specIds;
    private String status;
    private Integer sortOrder;
}
