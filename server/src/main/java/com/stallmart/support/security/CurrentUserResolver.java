package com.stallmart.support.security;

import com.stallmart.support.exception.ErrorCode;
import com.stallmart.support.exception.AppException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

@Component
public class CurrentUserResolver {

    private final JwtService jwtService;

    public CurrentUserResolver(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    public long resolve(HttpServletRequest request) {
        String userId = request.getHeader("X-User-Id");
        if (userId != null && !userId.isBlank()) {
            return parseLong(userId);
        }

        String authorization = request.getHeader("Authorization");
        if (authorization != null && authorization.startsWith("Bearer ")) {
            String token = authorization.substring("Bearer ".length());
            return resolveBearerToken(token);
        }

        return 1L;
    }

    public long resolveRefreshToken(String refreshToken) {
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

    private long resolveBearerToken(String token) {
        if (token.startsWith("access-")) {
            String tokenTail = token.substring("access-".length());
            int delimiter = tokenTail.indexOf('-');
            if (delimiter > 0) {
                return parseLong(tokenTail.substring(0, delimiter));
            }
        }
        try {
            if (jwtService.isExpired(token)) {
                throw new AppException(ErrorCode.TOKEN_EXPIRED);
            }
            return jwtService.getUserId(token);
        } catch (AppException exception) {
            throw exception;
        } catch (RuntimeException exception) {
            throw new AppException(ErrorCode.TOKEN_INVALID);
        }
    }

    private long parseLong(String value) {
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException exception) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }
    }
}
