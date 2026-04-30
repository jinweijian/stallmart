package com.stallmart.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 规格实体
 */
@Data
@TableName("product_spec")
public class ProductSpec {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 风格包ID */
    private Long styleId;

    /** 规格名称 */
    private String name;

    /** 规格描述 */
    private String description;

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
