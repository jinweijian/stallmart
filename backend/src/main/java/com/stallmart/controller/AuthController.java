package com.stallmart.controller;

import com.stallmart.common.result.Result;
import com.stallmart.model.dto.BindPhoneDTO;
import com.stallmart.model.dto.LogoutDTO;
import com.stallmart.model.dto.RefreshTokenDTO;
import com.stallmart.model.dto.WechatLoginDTO;
import com.stallmart.model.entity.User;
import com.stallmart.model.vo.AuthVO;
import com.stallmart.model.vo.UserVO;
import com.stallmart.service.AuthService;
import com.stallmart.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器
 */
@Tag(name = "认证", description = "微信登录、Token刷新、登出")
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    @Operation(summary = "微信登录")
    @PostMapping("/wechat/login")
    public Result<AuthVO> wechatLogin(@Valid @RequestBody WechatLoginDTO dto) {
        AuthVO result = (AuthVO) authService.wechatLogin(dto);
        return Result.success(result);
    }

    @Operation(summary = "绑定手机号")
    @PostMapping("/phone/bind")
    public Result<AuthVO> bindPhone(@Valid @RequestBody BindPhoneDTO dto, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        AuthVO result = (AuthVO) authService.bindPhone(dto, userId);
        return Result.success(result);
    }

    @Operation(summary = "刷新Token")
    @PostMapping("/refresh")
    public Result<AuthVO> refresh(@Valid @RequestBody RefreshTokenDTO dto) {
        AuthVO result = (AuthVO) authService.refresh(dto);
        return Result.success(result);
    }

    @Operation(summary = "登出")
    @PostMapping("/logout")
    public Result<Void> logout(@Valid @RequestBody LogoutDTO dto, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        authService.logout(dto, userId);
        return Result.success();
    }
}
