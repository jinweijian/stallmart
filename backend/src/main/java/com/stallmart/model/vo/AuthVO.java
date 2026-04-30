package com.stallmart.model.vo;

import lombok.Data;
import lombok.Builder;

/**
 * 认证响应 VO
 */
@Data
@Builder
public class AuthVO {

    /** Access Token */
    private String accessToken;

    /** Refresh Token */
    private String refreshToken;

    /** 过期时间（秒） */
    private Integer expiresIn;

    /** 用户信息 */
    private UserVO userInfo;
}
