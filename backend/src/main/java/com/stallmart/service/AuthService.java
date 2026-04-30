package com.stallmart.service;

import com.stallmart.model.dto.BindPhoneDTO;
import com.stallmart.model.dto.LogoutDTO;
import com.stallmart.model.dto.RefreshTokenDTO;
import com.stallmart.model.dto.WechatLoginDTO;

/**
 * 认证服务接口
 */
public interface AuthService {

    /**
     * 微信登录
     */
    Object wechatLogin(WechatLoginDTO dto);

    /**
     * 绑定手机号
     */
    Object bindPhone(BindPhoneDTO dto, Long userId);

    /**
     * 刷新 Token
     */
    Object refresh(RefreshTokenDTO dto);

    /**
     * 登出
     */
    void logout(LogoutDTO dto, Long userId);
}
