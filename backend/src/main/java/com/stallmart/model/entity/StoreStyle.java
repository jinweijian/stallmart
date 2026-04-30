package com.stallmart.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 风格包实体
 */
@Data
@TableName("store_style")
public class StoreStyle {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 风格名称 */
    private String name;

    /** 主题：hawaii/bbq/market/ocean/fresh */
    private String theme;

    /** 风格配置JSON */
    private String config;

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
