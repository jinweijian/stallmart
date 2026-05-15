package com.stallmart.management.internal.api;

import com.stallmart.auth.dto.AdminLoginParams;
import com.stallmart.auth.dto.AdminSessionDTO;
import com.stallmart.auth.dto.AdminCaptchaDTO;
import com.stallmart.auth.dto.AdminLoginFailureDTO;
import com.stallmart.auth.internal.service.AdminLoginSecurityService;
import com.stallmart.management.OperationLogService;
import com.stallmart.management.dto.OperationLogRecordParams;
import com.stallmart.management.internal.model.OperationLogResult;
import com.stallmart.management.internal.model.OperationLogScope;
import com.stallmart.support.exception.AppException;
import com.stallmart.support.exception.ErrorCode;
import com.stallmart.support.security.CurrentUserResolver;
import com.stallmart.support.web.Result;
import com.stallmart.user.internal.model.UserRole;
import com.stallmart.user.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    private final AdminLoginSecurityService adminLoginSecurityService;
    private final OperationLogService operationLogService;

    public AdminAuthController(
            UserService userService,
            CurrentUserResolver currentUserResolver,
            AdminLoginSecurityService adminLoginSecurityService,
            OperationLogService operationLogService
    ) {
        this.userService = userService;
        this.currentUserResolver = currentUserResolver;
        this.adminLoginSecurityService = adminLoginSecurityService;
        this.operationLogService = operationLogService;
    }

    @PostMapping("/login")
    public ResponseEntity<Result<?>> login(@Valid @RequestBody AdminLoginParams params, HttpServletRequest request) {
        String ipAddress = clientIp(request);
        if (adminLoginSecurityService.isCaptchaRequired(params.account(), ipAddress)
                && (params.captchaId() == null || params.captchaAnswer() == null)) {
            recordLoginFailure(params.account(), ipAddress, request, ErrorCode.CAPTCHA_REQUIRED.message());
            return loginError(ErrorCode.CAPTCHA_REQUIRED);
        }
        if (adminLoginSecurityService.isCaptchaRequired(params.account(), ipAddress)
                && !adminLoginSecurityService.verifyCaptcha(params.captchaId(), params.captchaAnswer())) {
            recordLoginFailure(params.account(), ipAddress, request, ErrorCode.CAPTCHA_INVALID.message());
            return loginError(ErrorCode.CAPTCHA_INVALID);
        }
        try {
            AdminSessionDTO session = userService.adminLogin(params.account(), params.password());
            adminLoginSecurityService.clearFailures(params.account(), ipAddress);
            recordLoginSuccess(params.account(), session, ipAddress, request);
            return ResponseEntity.ok(Result.success(session));
        } catch (AppException exception) {
            if (exception.getErrorCode() != ErrorCode.INVALID_CREDENTIALS) {
                throw exception;
            }
            int failures = adminLoginSecurityService.recordFailure(params.account(), ipAddress);
            recordLoginFailure(params.account(), ipAddress, request, ErrorCode.INVALID_CREDENTIALS.message());
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Result.error(
                            ErrorCode.INVALID_CREDENTIALS.code(),
                            ErrorCode.INVALID_CREDENTIALS.message(),
                            new AdminLoginFailureDTO(failures >= 3)
                    ));
        }
    }

    @GetMapping("/captcha")
    public Result<AdminCaptchaDTO> captcha() {
        return Result.success(adminLoginSecurityService.createCaptcha());
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

    private ResponseEntity<Result<?>> loginError(ErrorCode errorCode) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(Result.error(errorCode.code(), errorCode.message(), new AdminLoginFailureDTO(true)));
    }

    private void recordLoginSuccess(String account, AdminSessionDTO session, String ipAddress, HttpServletRequest request) {
        operationLogService.record(new OperationLogRecordParams(
                session.user().role() == UserRole.ADMIN ? OperationLogScope.PLATFORM : OperationLogScope.VENDOR,
                session.user().role() == UserRole.ADMIN ? null : session.storeId(),
                session.user().id(),
                account,
                session.user().role(),
                "LOGIN_SUCCESS",
                "ADMIN_AUTH",
                null,
                "后台登录成功",
                OperationLogResult.SUCCESS,
                ipAddress,
                request.getHeader("User-Agent")
        ));
    }

    private void recordLoginFailure(String account, String ipAddress, HttpServletRequest request, String reason) {
        operationLogService.record(new OperationLogRecordParams(
                OperationLogScope.PLATFORM,
                null,
                null,
                account,
                null,
                "LOGIN_FAILURE",
                "ADMIN_AUTH",
                null,
                "后台登录失败：" + reason,
                OperationLogResult.FAILURE,
                ipAddress,
                request.getHeader("User-Agent")
        ));
    }

    private String clientIp(HttpServletRequest request) {
        String forwardedFor = request.getHeader("X-Forwarded-For");
        if (forwardedFor != null && !forwardedFor.isBlank()) {
            return forwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
