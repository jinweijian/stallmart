package com.stallmart.auth.dto;

import com.stallmart.user.dto.UserProfileDTO;

public record AdminSessionDTO(
        String accessToken,
        String refreshToken,
        UserProfileDTO user,
        Long storeId,
        String entryPath
) {
}
