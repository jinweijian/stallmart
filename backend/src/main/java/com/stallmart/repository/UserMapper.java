package com.stallmart.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.stallmart.model.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户 Mapper
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
}
