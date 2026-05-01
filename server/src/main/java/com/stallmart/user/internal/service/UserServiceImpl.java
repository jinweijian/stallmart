package com.stallmart.user.internal.service;

import com.stallmart.support.exception.ErrorCode;
import com.stallmart.support.exception.AppException;
import com.stallmart.support.security.JwtService;
import com.stallmart.auth.dto.AdminSessionDTO;
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
    private final Map<String, AdminAccount> adminAccounts = new ConcurrentHashMap<>();
    private final JwtService jwtService;

    public UserServiceImpl(JwtService jwtService) {
        this.jwtService = jwtService;
        users.put(1L, new UserProfileDTO(1L, "市集顾客", "/static/default-avatar.png", null, false, "CUSTOMER"));
        users.put(2L, new UserProfileDTO(2L, "海边摊主", "/static/vendor-avatar.png", "139****1201", true, "VENDOR"));
        users.put(99L, new UserProfileDTO(99L, "平台管理员", "/static/admin-avatar.png", "138****0099", true, "ADMIN"));
        adminAccounts.put("platform", new AdminAccount("platform", "platform123", 99L, null, "/platform/vendors"));
        adminAccounts.put("vendor", new AdminAccount("vendor", "vendor123", 2L, 1L, "/vendor"));
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
        return tokenFor(user);
    }

    @Override
    public AdminSessionDTO adminLogin(String account, String password) {
        AdminAccount adminAccount = adminAccounts.get(account);
        if (adminAccount == null || !adminAccount.password().equals(password)) {
            throw new AppException(ErrorCode.INVALID_CREDENTIALS);
        }
        UserProfileDTO user = getProfile(adminAccount.userId());
        return adminSession(user, adminAccount.storeId(), adminAccount.entryPath());
    }

    @Override
    public AdminSessionDTO adminSession(long userId) {
        UserProfileDTO user = getProfile(userId);
        AdminAccount account = adminAccounts.values().stream()
                .filter(candidate -> candidate.userId().equals(user.id()))
                .findFirst()
                .orElse(null);
        Long storeId = account == null ? null : account.storeId();
        String entryPath = user.role().equals("ADMIN") ? "/platform/vendors" : "/vendor";
        return adminSession(user, storeId, entryPath);
    }

    @Override
    public AuthTokenDTO refresh(String refreshToken) {
        long userId = parseRefreshUserId(refreshToken);
        UserProfileDTO user = getProfile(userId);
        return tokenFor(user);
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
            return parseJwtRefreshUserId(refreshToken);
        }
        try {
            return Long.parseLong(refreshToken.substring("refresh-".length()));
        } catch (NumberFormatException exception) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }
    }

    private long parseJwtRefreshUserId(String refreshToken) {
        try {
            if (refreshToken == null || jwtService.isExpired(refreshToken)) {
                throw new AppException(ErrorCode.TOKEN_EXPIRED);
            }
            return jwtService.getUserId(refreshToken);
        } catch (AppException exception) {
            throw exception;
        } catch (RuntimeException exception) {
            throw new AppException(ErrorCode.TOKEN_INVALID);
        }
    }

    private AuthTokenDTO tokenFor(UserProfileDTO user) {
        return new AuthTokenDTO(
                jwtService.generateAccessToken(user.id(), user.role(), user.hasPhone()),
                jwtService.generateRefreshToken(user.id()),
                user
        );
    }

    private AdminSessionDTO adminSession(UserProfileDTO user, Long storeId, String entryPath) {
        return new AdminSessionDTO(
                jwtService.generateAccessToken(user.id(), user.role(), user.hasPhone()),
                jwtService.generateRefreshToken(user.id()),
                user,
                storeId,
                entryPath
        );
    }

    private record AdminAccount(String account, String password, Long userId, Long storeId, String entryPath) {
    }
}
