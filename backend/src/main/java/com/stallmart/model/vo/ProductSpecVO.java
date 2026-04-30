package com.stallmart.model.vo;

import lombok.Data;
import lombok.Builder;

@Data
@Builder
public class ProductSpecVO {
    private Long id;
    private Long styleId;
    private String name;
    private String description;
    private Integer sortOrder;
}
