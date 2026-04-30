package com.stallmart.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 登出 DTO
 */
@Data
public class LogoutDTO {

    @NotBlank(message = "refresh_token 不能为空")
    private String refreshToken;
}
