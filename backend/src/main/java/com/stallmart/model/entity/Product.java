package com.stallmart.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.math.BigDecimal;

/**
 * 商品实体
 */
@Data
@TableName("product")
public class Product {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 店铺ID */
    private Long storeId;

    /** 商品名称 */
    private String name;

    /** 商品描述 */
    private String description;

    /** 价格 */
    private BigDecimal price;

    /** 商品图片 */
    private String imageUrl;

    /** 关联规格ID列表 */
    private String specIds;

    /** 状态：active/off_sale */
    private String status;

    /** 排序 */
    private Integer sortOrder;

    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /** 更新时间 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    /** 逻辑删除 */
    @TableLogic
    private Boolean deleted;
}
