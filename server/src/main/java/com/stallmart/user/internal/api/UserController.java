package com.stallmart.user.internal.api;

import com.stallmart.support.web.Result;
import com.stallmart.support.security.CurrentUserResolver;
import com.stallmart.user.dto.UpdateProfileParams;
import com.stallmart.user.dto.UserProfileDTO;
import com.stallmart.user.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final CurrentUserResolver currentUserResolver;

    public UserController(UserService userService, CurrentUserResolver currentUserResolver) {
        this.userService = userService;
        this.currentUserResolver = currentUserResolver;
    }

    @GetMapping("/profile")
    public Result<UserProfileDTO> profile(HttpServletRequest request) {
        return Result.success(userService.getProfile(currentUserResolver.resolve(request)));
    }

    @PutMapping("/profile")
    public Result<UserProfileDTO> updateProfile(@RequestBody UpdateProfileParams request, HttpServletRequest servletRequest) {
        return Result.success(userService.updateProfile(currentUserResolver.resolve(servletRequest), request));
    }
}
