package com.stallmart.model.vo;

import lombok.Data;
import lombok.Builder;

/**
 * 用户信息 VO
 */
@Data
@Builder
public class UserVO {

    private Long id;
    private String nickname;
    private String avatarUrl;
    private String phone;
    private Boolean hasPhone;
    private String role;
}
