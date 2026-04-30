package com.stallmart.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.stallmart.common.exception.BusinessException;
import com.stallmart.common.result.ErrorCode;
import com.stallmart.model.entity.Store;
import com.stallmart.repository.StoreMapper;
import com.stallmart.service.StoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 店铺服务实现
 */
@Service
@RequiredArgsConstructor
public class StoreServiceImpl implements StoreService {

    private final StoreMapper storeMapper;

    @Override
    public Store findById(Long id) {
        Store store = storeMapper.selectById(id);
        if (store == null || store.getDeleted()) {
            throw new BusinessException(ErrorCode.STORE_NOT_FOUND);
        }
        return store;
    }

    @Override
    public Store findByQrCode(String qrCode) {
        Store store = storeMapper.selectOne(
            new LambdaQueryWrapper<Store>()
                .eq(Store::getQrCode, qrCode)
                .eq(Store::getDeleted, false)
        );
        if (store == null) {
            throw new BusinessException(ErrorCode.STORE_NOT_FOUND);
        }
        return store;
    }

    @Override
    public List<Store> findByOwnerId(Long ownerId) {
        return storeMapper.selectList(
            new LambdaQueryWrapper<Store>()
                .eq(Store::getOwnerId, ownerId)
                .eq(Store::getDeleted, false)
        );
    }

    @Override
    public Store create(Store store) {
        store.setCreatedAt(LocalDateTime.now());
        store.setUpdatedAt(LocalDateTime.now());
        store.setDeleted(false);
        storeMapper.insert(store);
        return store;
    }

    @Override
    public void update(Store store) {
        store.setUpdatedAt(LocalDateTime.now());
        storeMapper.updateById(store);
    }
}
