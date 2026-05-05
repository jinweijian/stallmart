package com.stallmart.user.internal.service;

import com.stallmart.support.exception.ErrorCode;
import com.stallmart.support.exception.AppException;
import com.stallmart.support.security.JwtService;
import com.stallmart.auth.dto.AdminSessionDTO;
import com.stallmart.user.dto.UpdateProfileParams;
import com.stallmart.auth.dto.AuthTokenDTO;
import com.stallmart.user.dto.UserProfileDTO;
import com.stallmart.user.UserService;
import java.util.List;
import com.stallmart.user.internal.repository.AdminAccountEntity;
import com.stallmart.user.internal.repository.AdminAccountRepository;
import com.stallmart.user.internal.repository.UserEntity;
import com.stallmart.user.internal.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserService {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final AdminAccountRepository adminAccountRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(
            JwtService jwtService,
            UserRepository userRepository,
            AdminAccountRepository adminAccountRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.adminAccountRepository = adminAccountRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public AuthTokenDTO login(String code, String nickname, String avatarUrl) {
        UserEntity user = new UserEntity();
        user.openId = code == null || code.isBlank() ? "dev-openid-" + System.nanoTime() : code;
        user.nickname = nickname == null || nickname.isBlank() ? "微信用户" : nickname;
        user.avatarUrl = avatarUrl;
        user.hasPhone = false;
        user.role = "CUSTOMER";
        user.status = "ACTIVE";
        return tokenFor(toDto(userRepository.save(user)));
    }

    @Override
    public AdminSessionDTO adminLogin(String account, String password) {
        AdminAccountEntity adminAccount = adminAccountRepository.findByAccount(account)
                .orElseThrow(() -> new AppException(ErrorCode.INVALID_CREDENTIALS));
        if (!"ACTIVE".equals(adminAccount.status) || !passwordEncoder.matches(password, adminAccount.passwordHash)) {
            throw new AppException(ErrorCode.INVALID_CREDENTIALS);
        }
        UserProfileDTO user = getProfile(adminAccount.userId);
        return adminSession(user, adminAccount.storeId, adminAccount.entryPath);
    }

    @Override
    public AdminSessionDTO adminSession(long userId) {
        UserProfileDTO user = getProfile(userId);
        AdminAccountEntity account = adminAccountRepository.findByUserId(user.id()).orElse(null);
        Long storeId = account == null ? null : account.storeId;
        String entryPath = account == null
                ? (user.role().equals("ADMIN") ? "/platform/vendors" : "/vendor")
                : account.entryPath;
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
        return userRepository.findAllByOrderByIdAsc().stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    @Transactional
    public UserProfileDTO bindPhone(long userId, String phoneCode) {
        UserEntity current = getUser(userId);
        String suffix = phoneCode.length() > 4 ? phoneCode.substring(phoneCode.length() - 4) : "0000";
        current.phone = "138****" + suffix;
        current.hasPhone = true;
        return toDto(userRepository.save(current));
    }

    @Override
    public UserProfileDTO getProfile(long userId) {
        return toDto(getUser(userId));
    }

    @Override
    @Transactional
    public UserProfileDTO updateProfile(long userId, UpdateProfileParams request) {
        UserEntity current = getUser(userId);
        current.nickname = request.nickname() == null ? current.nickname : request.nickname();
        current.avatarUrl = request.avatarUrl() == null ? current.avatarUrl : request.avatarUrl();
        return toDto(userRepository.save(current));
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

    private UserEntity getUser(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));
    }

    private UserProfileDTO toDto(UserEntity user) {
        return new UserProfileDTO(
                user.id,
                user.nickname,
                user.avatarUrl,
                user.phone,
                user.hasPhone,
                user.role
        );
    }
}
