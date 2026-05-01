package com.stallmart.support.security;

import com.stallmart.support.exception.ErrorCode;
import com.stallmart.support.exception.AppException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

@Component
public class CurrentUserResolver {

    public long resolve(HttpServletRequest request) {
        String userId = request.getHeader("X-User-Id");
        if (userId != null && !userId.isBlank()) {
            return parseLong(userId);
        }

        String authorization = request.getHeader("Authorization");
        if (authorization != null && authorization.startsWith("Bearer access-")) {
            String tokenTail = authorization.substring("Bearer access-".length());
            int delimiter = tokenTail.indexOf('-');
            if (delimiter > 0) {
                return parseLong(tokenTail.substring(0, delimiter));
            }
        }

        return 1L;
    }

    private long parseLong(String value) {
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException exception) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }
    }
}
