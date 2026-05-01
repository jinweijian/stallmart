package com.stallmart.user;

import com.stallmart.user.dto.UpdateProfileParams;
import com.stallmart.auth.dto.AuthTokenDTO;
import com.stallmart.user.dto.UserProfileDTO;
import java.util.List;

public interface UserService {

    AuthTokenDTO login(String code, String nickname, String avatarUrl);

    AuthTokenDTO refresh(String refreshToken);

    List<UserProfileDTO> listUsers();

    UserProfileDTO bindPhone(long userId, String phoneCode);

    UserProfileDTO getProfile(long userId);

    UserProfileDTO updateProfile(long userId, UpdateProfileParams request);
}
