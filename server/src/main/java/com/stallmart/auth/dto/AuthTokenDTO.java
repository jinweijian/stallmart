package com.stallmart.auth.dto;

import com.stallmart.user.dto.UserProfileDTO;

public record AuthTokenDTO(String accessToken, String refreshToken, UserProfileDTO user) {
}
