package com.stallmart.user.dto;

public record UserProfileDTO(
        Long id,
        String nickname,
        String avatarUrl,
        String phone,
        boolean hasPhone,
        String role
) {
}
