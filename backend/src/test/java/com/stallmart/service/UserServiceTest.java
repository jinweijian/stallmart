package com.stallmart.service;

import com.stallmart.model.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Test
    void testCreateOrUpdate_NewUser() {
        String openid = "test_openid_" + System.currentTimeMillis();
        User user = userService.createOrUpdate(openid, "unionid", "测试用户", "https://example.com/avatar.png");

        assertNotNull(user);
        assertNotNull(user.getId());
        assertEquals(openid, user.getOpenid());
        assertEquals("测试用户", user.getNickname());
        assertFalse(user.getHasPhone());
        assertEquals("customer", user.getRole());
    }

    @Test
    void testCreateOrUpdate_ExistingUser() {
        String openid = "test_openid_existing";
        User user1 = userService.createOrUpdate(openid, "unionid", "用户1", null);
        User user2 = userService.createOrUpdate(openid, "unionid", "用户2", null);

        assertEquals(user1.getId(), user2.getId());
        assertEquals("用户2", user2.getNickname());
    }

    @Test
    void testFindByOpenid() {
        String openid = "test_find_openid";
        userService.createOrUpdate(openid, "unionid", "查找测试", null);

        User user = userService.findByOpenid(openid);
        assertNotNull(user);
        assertEquals(openid, user.getOpenid());
    }

    @Test
    void testUpdatePhone() {
        String openid = "test_phone_openid";
        User user = userService.createOrUpdate(openid, "unionid", "手机测试", null);

        userService.updatePhone(user.getId(), "13800138000");

        User updated = userService.findById(user.getId());
        assertTrue(updated.getHasPhone());
        assertEquals("13800138000", updated.getPhone());
    }
}
