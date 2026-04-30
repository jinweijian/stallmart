package com.stallmart.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.stallmart.model.entity.Order;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderMapper extends BaseMapper<Order> {
}
