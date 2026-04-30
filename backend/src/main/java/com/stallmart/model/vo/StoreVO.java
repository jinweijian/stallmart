package com.stallmart.model.vo;

import lombok.Data;
import lombok.Builder;

/**
 * 店铺信息 VO
 */
@Data
@Builder
public class StoreVO {
    private Long id;
    private String name;
    private String description;
    private String avatarUrl;
    private String qrCode;
    private Long styleId;
    private String status;
}
