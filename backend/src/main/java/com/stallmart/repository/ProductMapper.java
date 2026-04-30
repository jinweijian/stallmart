package com.stallmart.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.stallmart.model.entity.Product;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品 Mapper
 */
@Mapper
public interface ProductMapper extends BaseMapper<Product> {
}
