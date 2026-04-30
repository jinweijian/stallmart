package com.stallmart.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.stallmart.model.entity.Store;
import org.apache.ibatis.annotations.Mapper;

/**
 * 店铺 Mapper
 */
@Mapper
public interface StoreMapper extends BaseMapper<Store> {
}
