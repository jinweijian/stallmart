package com.stallmart.service;

import com.stallmart.model.entity.Store;
import java.util.List;

/**
 * 店铺服务接口
 */
public interface StoreService {
    Store findById(Long id);
    Store findByQrCode(String qrCode);
    List<Store> findByOwnerId(Long ownerId);
    Store create(Store store);
    void update(Store store);
}
