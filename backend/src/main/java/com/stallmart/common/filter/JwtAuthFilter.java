package com.stallmart.common.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stallmart.common.result.ErrorCode;
import com.stallmart.common.result.Result;
import com.stallmart.config.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * JWT 认证过滤器
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final ObjectMapper objectMapper;

    // 公开接口（无需认证）
    private static final List<String> PUBLIC_PATHS = Arrays.asList(
            "/api/v1/stores/qr/**",
            "/api/v1/auth/**",
            "/api/v1/styles/**",
            "/swagger-ui/**",
            "/api-docs/**",
            "/v3/api-docs/**"
    );

    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {

        String path = request.getRequestURI();

        // 公开接口直接放行
        if (isPublicPath(path)) {
            chain.doFilter(request, response);
            return;
        }

        // 提取 Token
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            sendUnauthorized(response, ErrorCode.UNAUTHORIZED.getCode(), "token_missing");
            return;
        }

        String token = authHeader.substring(7);

        try {
            // 验证 Token
            if (jwtService.isTokenExpired(token)) {
                sendUnauthorized(response, ErrorCode.TOKEN_EXPIRED.getCode(), ErrorCode.TOKEN_EXPIRED.getMessage());
                return;
            }

            // 解析用户信息
            Long userId = jwtService.getUserIdFromToken(token);

            // 存入请求属性，供后续使用
            request.setAttribute("userId", userId);

            chain.doFilter(request, response);

        } catch (Exception e) {
            log.warn("JWT 验证失败: {}", e.getMessage());
            sendUnauthorized(response, ErrorCode.TOKEN_INVALID.getCode(), ErrorCode.TOKEN_INVALID.getMessage());
        }
    }

    private boolean isPublicPath(String path) {
        return PUBLIC_PATHS.stream()
                .anyMatch(pattern -> pathMatcher.match(pattern, path));
    }

    private void sendUnauthorized(HttpServletResponse response, int code, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(Result.error(code, message)));
    }
}
