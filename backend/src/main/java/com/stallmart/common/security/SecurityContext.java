package com.stallmart.common.security;

import lombok.Getter;

/**
 * 安全上下文工具
 * 用于在请求中传递当前登录用户信息
 */
@Getter
public class SecurityContext {

    private static final ThreadLocal<Long> currentUserId = new ThreadLocal<>();
    private static final ThreadLocal<String> currentRole = new ThreadLocal<>();

    public static void setCurrentUser(Long userId, String role) {
        currentUserId.set(userId);
        currentRole.set(role);
    }

    public static Long getCurrentUserId() {
        return currentUserId.get();
    }

    public static void clear() {
        currentUserId.remove();
        currentRole.remove();
    }
}
