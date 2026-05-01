package com.stallmart.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record BindPhoneParams(@NotBlank String phoneCode) {
}
