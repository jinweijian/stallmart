package com.stallmart.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 绑定手机号 DTO
 */
@Data
public class BindPhoneDTO {

    /** 微信授权 code */
    @NotBlank(message = "code 不能为空")
    private String code;

    /** 加密数据 */
    @NotBlank(message = "encryptedData 不能为空")
    private String encryptedData;

    /** IV */
    @NotBlank(message = "iv 不能为空")
    private String iv;
}
