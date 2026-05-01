package com.stallmart.user.internal.service;

import com.stallmart.support.exception.ErrorCode;
import com.stallmart.support.exception.AppException;
import com.stallmart.user.dto.UpdateProfileParams;
import com.stallmart.auth.dto.AuthTokenDTO;
import com.stallmart.user.dto.UserProfileDTO;
import com.stallmart.user.UserService;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final AtomicLong idSequence = new AtomicLong(100);
    private final Map<Long, UserProfileDTO> users = new ConcurrentHashMap<>();

    public UserServiceImpl() {
        users.put(1L, new UserProfileDTO(1L, "市集顾客", "/static/default-avatar.png", null, false, "CUSTOMER"));
        users.put(2L, new UserProfileDTO(2L, "海边摊主", "/static/vendor-avatar.png", "139****1201", true, "VENDOR"));
        users.put(99L, new UserProfileDTO(99L, "平台管理员", "/static/admin-avatar.png", "138****0099", true, "ADMIN"));
    }

    @Override
    public AuthTokenDTO login(String code, String nickname, String avatarUrl) {
        long userId = idSequence.getAndUpdate(value -> Math.max(value + 1, 2));
        UserProfileDTO user = new UserProfileDTO(
                userId,
                nickname == null || nickname.isBlank() ? "微信用户" : nickname,
                avatarUrl,
                null,
                false,
                "CUSTOMER"
        );
        users.put(userId, user);
        return new AuthTokenDTO("access-" + userId + "-" + code, "refresh-" + userId, user);
    }

    @Override
    public AuthTokenDTO refresh(String refreshToken) {
        long userId = parseRefreshUserId(refreshToken);
        UserProfileDTO user = getProfile(userId);
        return new AuthTokenDTO("access-" + userId + "-refreshed", refreshToken, user);
    }

    @Override
    public List<UserProfileDTO> listUsers() {
        return users.values().stream()
                .sorted(Comparator.comparing(UserProfileDTO::id))
                .toList();
    }

    @Override
    public UserProfileDTO bindPhone(long userId, String phoneCode) {
        UserProfileDTO current = getProfile(userId);
        String suffix = phoneCode.length() > 4 ? phoneCode.substring(phoneCode.length() - 4) : "0000";
        UserProfileDTO updated = new UserProfileDTO(
                current.id(),
                current.nickname(),
                current.avatarUrl(),
                "138****" + suffix,
                true,
                current.role()
        );
        users.put(userId, updated);
        return updated;
    }

    @Override
    public UserProfileDTO getProfile(long userId) {
        UserProfileDTO user = users.get(userId);
        if (user == null) {
            throw new AppException(ErrorCode.NOT_FOUND);
        }
        return user;
    }

    @Override
    public UserProfileDTO updateProfile(long userId, UpdateProfileParams request) {
        UserProfileDTO current = getProfile(userId);
        UserProfileDTO updated = new UserProfileDTO(
                current.id(),
                request.nickname() == null ? current.nickname() : request.nickname(),
                request.avatarUrl() == null ? current.avatarUrl() : request.avatarUrl(),
                current.phone(),
                current.hasPhone(),
                current.role()
        );
        users.put(userId, updated);
        return updated;
    }

    private long parseRefreshUserId(String refreshToken) {
        if (refreshToken == null || !refreshToken.startsWith("refresh-")) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }
        try {
            return Long.parseLong(refreshToken.substring("refresh-".length()));
        } catch (NumberFormatException exception) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }
    }
}
