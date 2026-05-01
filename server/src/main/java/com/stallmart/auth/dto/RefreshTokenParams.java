package com.stallmart.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record RefreshTokenParams(@NotBlank String refreshToken) {
}
