package com.stallmart.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 微信登录 DTO
 */
@Data
public class WechatLoginDTO {

    /** 微信授权 code */
    @NotBlank(message = "code 不能为空")
    private String code;
}
