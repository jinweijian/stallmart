package com.stallmart.auth.internal.api;

import com.stallmart.support.web.Result;
import com.stallmart.support.security.CurrentUserResolver;
import com.stallmart.auth.dto.BindPhoneParams;
import com.stallmart.auth.dto.RefreshTokenParams;
import com.stallmart.auth.dto.WechatLoginParams;
import com.stallmart.auth.dto.AuthTokenDTO;
import com.stallmart.user.dto.UserProfileDTO;
import com.stallmart.user.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;
    private final CurrentUserResolver currentUserResolver;

    public AuthController(UserService userService, CurrentUserResolver currentUserResolver) {
        this.userService = userService;
        this.currentUserResolver = currentUserResolver;
    }

    @PostMapping("/wechat/login")
    public Result<AuthTokenDTO> wechatLogin(@Valid @RequestBody WechatLoginParams request) {
        return Result.success(userService.login(request.code(), request.nickname(), request.avatarUrl()));
    }

    @PostMapping("/refresh")
    public Result<AuthTokenDTO> refresh(@Valid @RequestBody RefreshTokenParams request) {
        return Result.success(userService.refresh(request.refreshToken()));
    }

    @PostMapping("/phone/bind")
    public Result<UserProfileDTO> bindPhone(@Valid @RequestBody BindPhoneParams request, HttpServletRequest servletRequest) {
        long userId = currentUserResolver.resolve(servletRequest);
        return Result.success(userService.bindPhone(userId, request.phoneCode()));
    }

    @PostMapping("/logout")
    public Result<Void> logout() {
        return Result.success(null);
    }
}
