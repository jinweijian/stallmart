package com.stallmart.management.internal.api;

import com.stallmart.auth.dto.AdminLoginParams;
import com.stallmart.auth.dto.AdminSessionDTO;
import com.stallmart.support.security.CurrentUserResolver;
import com.stallmart.support.web.Result;
import com.stallmart.user.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/auth")
public class AdminAuthController {

    private final UserService userService;
    private final CurrentUserResolver currentUserResolver;

    public AdminAuthController(UserService userService, CurrentUserResolver currentUserResolver) {
        this.userService = userService;
        this.currentUserResolver = currentUserResolver;
    }

    @PostMapping("/login")
    public Result<AdminSessionDTO> login(@Valid @RequestBody AdminLoginParams params) {
        return Result.success(userService.adminLogin(params.account(), params.password()));
    }

    @PostMapping("/refresh")
    public Result<AdminSessionDTO> refresh(@RequestBody RefreshAdminTokenParams params) {
        long userId = currentUserResolver.resolveRefreshToken(params.refreshToken());
        return Result.success(userService.adminSession(userId));
    }

    @GetMapping("/me")
    public Result<AdminSessionDTO> me(HttpServletRequest request) {
        return Result.success(userService.adminSession(currentUserResolver.resolve(request)));
    }

    public record RefreshAdminTokenParams(String refreshToken) {
    }
}
