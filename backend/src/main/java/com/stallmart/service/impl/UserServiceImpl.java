package com.stallmart.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.stallmart.common.exception.BusinessException;
import com.stallmart.common.result.ErrorCode;
import com.stallmart.model.entity.User;
import com.stallmart.repository.UserMapper;
import com.stallmart.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 用户服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;

    @Override
    public User findByOpenid(String openid) {
        return userMapper.selectOne(
            new LambdaQueryWrapper<User>()
                .eq(User::getOpenid, openid)
                .eq(User::getDeleted, false)
        );
    }

    @Override
    public User findById(Long id) {
        User user = userMapper.selectById(id);
        if (user == null || user.getDeleted()) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }
        return user;
    }

    @Override
    public User findByPhone(String phone) {
        return userMapper.selectOne(
            new LambdaQueryWrapper<User>()
                .eq(User::getPhone, phone)
                .eq(User::getDeleted, false)
        );
    }

    @Override
    public User createOrUpdate(String openid, String unionid, String nickname, String avatarUrl) {
        User existingUser = findByOpenid(openid);

        if (existingUser != null) {
            // 更新用户信息
            existingUser.setNickname(nickname);
            existingUser.setAvatarUrl(avatarUrl);
            existingUser.setUpdatedAt(LocalDateTime.now());
            userMapper.updateById(existingUser);
            log.info("更新已有用户: openid={}, id={}", openid, existingUser.getId());
            return existingUser;
        }

        // 创建新用户
        User newUser = new User();
        newUser.setOpenid(openid);
        newUser.setUnionid(unionid);
        newUser.setNickname(nickname);
        newUser.setAvatarUrl(avatarUrl);
        newUser.setHasPhone(false);
        newUser.setRole("customer");
        newUser.setCreatedAt(LocalDateTime.now());
        newUser.setUpdatedAt(LocalDateTime.now());
        newUser.setDeleted(false);

        userMapper.insert(newUser);
        log.info("创建新用户: openid={}, id={}", openid, newUser.getId());
        return newUser;
    }

    @Override
    public void updatePhone(Long userId, String phone) {
        User user = findById(userId);
        user.setPhone(phone);
        user.setHasPhone(true);
        user.setUpdatedAt(LocalDateTime.now());
        userMapper.updateById(user);
        log.info("用户绑定手机号: userId={}, phone={}", userId, phone);
    }

    @Override
    public void updateProfile(Long userId, String nickname, String avatarUrl) {
        User user = findById(userId);
        if (nickname != null && !nickname.isBlank()) {
            user.setNickname(nickname);
        }
        if (avatarUrl != null) {
            user.setAvatarUrl(avatarUrl);
        }
        user.setUpdatedAt(LocalDateTime.now());
        userMapper.updateById(user);
        log.info("更新用户信息: userId={}", userId);
    }
}
