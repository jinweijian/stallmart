package com.stallmart.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.stallmart.model.entity.ProductSpec;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ProductSpecMapper extends BaseMapper<ProductSpec> {
}
