package com.stallmart.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record AdminLoginParams(@NotBlank String account, @NotBlank String password) {
}
