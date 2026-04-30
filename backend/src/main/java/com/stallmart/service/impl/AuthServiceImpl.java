package com.stallmart.service.impl;

import com.stallmart.common.exception.BusinessException;
import com.stallmart.common.result.ErrorCode;
import com.stallmart.config.JwtService;
import com.stallmart.model.dto.BindPhoneDTO;
import com.stallmart.model.dto.LogoutDTO;
import com.stallmart.model.dto.RefreshTokenDTO;
import com.stallmart.model.dto.WechatLoginDTO;
import com.stallmart.model.entity.User;
import com.stallmart.model.vo.AuthVO;
import com.stallmart.model.vo.UserVO;
import com.stallmart.service.AuthService;
import com.stallmart.service.UserService;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * 认证服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserService userService;
    private final JwtService jwtService;
    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${jwt.refresh-token-validity}")
    private long refreshTokenValidity;

    @Override
    public Object wechatLogin(WechatLoginDTO dto) {
        // TODO: 调用微信接口验证 code，获取 openid/unionid
        // 这里先用模拟数据
        String openid = "mock_openid_" + dto.getCode();
        String unionid = "mock_unionid";
        String nickname = "微信用户";
        String avatarUrl = "https://example.com/avatar.png";

        // 查询或创建用户
        User user = userService.createOrUpdate(openid, unionid, nickname, avatarUrl);

        // 生成 Token
        String accessToken = jwtService.generateAccessToken(user.getId(), user.getRole(), user.getHasPhone());
        String refreshToken = jwtService.generateRefreshToken(user.getId());

        return AuthVO.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresIn(7200)
                .userInfo(toUserVO(user))
                .build();
    }

    @Override
    public Object bindPhone(BindPhoneDTO dto, Long userId) {
        // TODO: 调用微信接口解密获取手机号
        String phone = "13800138000";

        // 更新用户手机号
        userService.updatePhone(userId, phone);

        // 重新查询用户
        User user = userService.findById(userId);

        // 生成新 Token
        String accessToken = jwtService.generateAccessToken(user.getId(), user.getRole(), user.getHasPhone());
        String refreshToken = jwtService.generateRefreshToken(user.getId());

        return AuthVO.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresIn(7200)
                .userInfo(toUserVO(user))
                .build();
    }

    @Override
    public Object refresh(RefreshTokenDTO dto) {
        String refreshToken = dto.getRefreshToken();

        // 解析 Token
        Claims claims;
        try {
            claims = jwtService.parseToken(refreshToken);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.TOKEN_INVALID);
        }

        // 检查是否在黑名单
        if (Boolean.TRUE.equals(redisTemplate.hasKey("blacklist:refresh:" + refreshToken))) {
            throw new BusinessException(ErrorCode.TOKEN_INVALID);
        }

        // 查询用户
        Long userId = Long.parseLong(claims.getSubject());
        User user = userService.findById(userId);

        // 生成新 Token
        String newAccessToken = jwtService.generateAccessToken(user.getId(), user.getRole(), user.getHasPhone());
        String newRefreshToken = jwtService.generateRefreshToken(user.getId());

        // 旧 Refresh Token 加入黑名单
        long expiration = claims.getExpiration().getTime();
        long ttl = (expiration - System.currentTimeMillis()) / 1000;
        if (ttl > 0) {
            redisTemplate.opsForValue().set("blacklist:refresh:" + refreshToken, "1", ttl, TimeUnit.SECONDS);
        }

        return AuthVO.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .expiresIn(7200)
                .build();
    }

    @Override
    public void logout(LogoutDTO dto, Long userId) {
        String refreshToken = dto.getRefreshToken();

        try {
            Claims claims = jwtService.parseToken(refreshToken);
            long expiration = claims.getExpiration().getTime();
            long ttl = (expiration - System.currentTimeMillis()) / 1000;
            if (ttl > 0) {
                redisTemplate.opsForValue().set("blacklist:refresh:" + refreshToken, "1", ttl, TimeUnit.SECONDS);
            }
        } catch (Exception e) {
            log.warn("登出时解析 refreshToken 失败", e);
        }
    }

    private UserVO toUserVO(User user) {
        return UserVO.builder()
                .id(user.getId())
                .nickname(user.getNickname())
                .avatarUrl(user.getAvatarUrl())
                .phone(maskPhone(user.getPhone()))
                .hasPhone(user.getHasPhone())
                .role(user.getRole())
                .build();
    }

    private String maskPhone(String phone) {
        if (phone == null || phone.length() < 11) return null;
        return phone.substring(0, 3) + "****" + phone.substring(7);
    }
}
