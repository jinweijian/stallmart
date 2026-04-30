package com.stallmart.controller;

import com.stallmart.common.result.Result;
import com.stallmart.model.dto.UpdateProfileDTO;
import com.stallmart.model.entity.User;
import com.stallmart.model.vo.UserVO;
import com.stallmart.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 用户控制器
 */
@Tag(name = "用户", description = "用户信息、编辑")
@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "获取用户信息")
    @GetMapping("/profile")
    public Result<UserVO> getProfile(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        User user = userService.findById(userId);
        return Result.success(toUserVO(user));
    }

    @Operation(summary = "更新用户信息")
    @PutMapping("/profile")
    public Result<Void> updateProfile(@Valid @RequestBody UpdateProfileDTO dto, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        userService.updateProfile(userId, dto.getNickname(), dto.getAvatarUrl());
        return Result.success();
    }

    private UserVO toUserVO(User user) {
        return UserVO.builder()
                .id(user.getId())
                .nickname(user.getNickname())
                .avatarUrl(user.getAvatarUrl())
                .phone(maskPhone(user.getPhone()))
                .hasPhone(user.getHasPhone())
                .role(user.getRole())
                .build();
    }

    private String maskPhone(String phone) {
        if (phone == null || phone.length() < 11) return null;
        return phone.substring(0, 3) + "****" + phone.substring(7);
    }
}
