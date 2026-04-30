package com.stallmart.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单实体
 */
@Data
@TableName("`order`")
public class Order {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 订单号 */
    private String orderNo;

    /** 顾客用户ID */
    private Long customerId;

    /** 店铺ID */
    private Long storeId;

    /** 订单总金额 */
    private BigDecimal totalAmount;

    /** 状态：pending/accepted/preparing/ready/completed/rejected/cancelled */
    private String status;

    /** 取餐确认码 */
    private String confirmCode;

    /** 备注 */
    private String remark;

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
