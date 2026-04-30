package com.stallmart.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 店铺实体
 */
@Data
@TableName("store")
public class Store {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 摊主用户ID */
    private Long ownerId;

    /** 店铺名称 */
    private String name;

    /** 店铺描述 */
    private String description;

    /** 店铺头像 */
    private String avatarUrl;

    /** 店铺二维码 */
    private String qrCode;

    /** 风格包ID */
    private Long styleId;

    /** 状态：active/closed */
    private String status;

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
