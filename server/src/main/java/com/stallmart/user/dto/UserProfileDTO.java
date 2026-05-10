package com.stallmart.user.dto;

import com.stallmart.user.internal.model.UserRole;

public record UserProfileDTO(
        Long id,
        String nickname,
        String avatarUrl,
        String phone,
        boolean hasPhone,
        UserRole role
) {
}
