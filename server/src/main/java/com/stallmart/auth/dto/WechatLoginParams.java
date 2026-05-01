package com.stallmart.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record WechatLoginParams(
        @NotBlank String code,
        String nickname,
        String avatarUrl
) {
}
