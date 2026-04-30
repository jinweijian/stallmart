package com.stallmart.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 刷新 Token DTO
 */
@Data
public class RefreshTokenDTO {

    @NotBlank(message = "refresh_token 不能为空")
    private String refreshToken;
}
