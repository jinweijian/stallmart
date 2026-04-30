package com.stallmart.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 用户实体
 */
@Data
@TableName("user")
public class User {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 微信 openid */
    private String openid;

    /** 微信 unionid */
    private String unionid;

    /** 昵称 */
    private String nickname;

    /** 头像 URL */
    private String avatarUrl;

    /** 手机号 */
    private String phone;

    /** 是否有手机号 */
    private Boolean hasPhone;

    /** 角色：customer / vendor / admin */
    private String role;

    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /** 更新时间 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    /** 逻辑删除标记 */
    @TableLogic
    private Boolean deleted;
}
