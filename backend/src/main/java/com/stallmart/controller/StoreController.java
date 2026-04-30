package com.stallmart.controller;

import com.stallmart.common.result.Result;
import com.stallmart.model.entity.Store;
import com.stallmart.model.vo.StoreVO;
import com.stallmart.service.StoreService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 店铺控制器
 */
@Tag(name = "店铺", description = "店铺信息、扫码进店")
@RestController
@RequestMapping("/api/v1/stores")
@RequiredArgsConstructor
public class StoreController {

    private final StoreService storeService;

    @Operation(summary = "获取店铺信息")
    @GetMapping("/{id}")
    public Result<StoreVO> getStore(@PathVariable Long id) {
        Store store = storeService.findById(id);
        return Result.success(toStoreVO(store));
    }

    @Operation(summary = "扫码进店")
    @GetMapping("/qr/{qrCode}")
    public Result<StoreVO> getStoreByQr(@PathVariable String qrCode) {
        Store store = storeService.findByQrCode(qrCode);
        return Result.success(toStoreVO(store));
    }

    private StoreVO toStoreVO(Store store) {
        return StoreVO.builder()
                .id(store.getId())
                .name(store.getName())
                .description(store.getDescription())
                .avatarUrl(store.getAvatarUrl())
                .qrCode(store.getQrCode())
                .styleId(store.getStyleId())
                .status(store.getStatus())
                .build();
    }
}
