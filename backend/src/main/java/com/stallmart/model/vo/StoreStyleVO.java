package com.stallmart.model.vo;

import lombok.Data;
import lombok.Builder;

@Data
@Builder
public class StoreStyleVO {
    private Long id;
    private String name;
    private String theme;
    private String config;
}
