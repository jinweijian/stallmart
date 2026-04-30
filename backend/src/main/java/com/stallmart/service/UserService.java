package com.stallmart.service;

import com.stallmart.model.entity.User;

/**
 * 用户服务接口
 */
public interface UserService {

    /**
     * 根据 openid 查找用户
     */
    User findByOpenid(String openid);

    /**
     * 根据 ID 查找用户
     */
    User findById(Long id);

    /**
     * 根据手机号查找用户
     */
    User findByPhone(String phone);

    /**
     * 创建或更新用户
     */
    User createOrUpdate(String openid, String unionid, String nickname, String avatarUrl);

    /**
     * 更新用户手机号
     */
    void updatePhone(Long userId, String phone);

    /**
     * 更新用户信息
     */
    void updateProfile(Long userId, String nickname, String avatarUrl);
}
