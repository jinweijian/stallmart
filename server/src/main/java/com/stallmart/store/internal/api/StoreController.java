package com.stallmart.store.internal.api;

import com.stallmart.support.web.Result;
import com.stallmart.store.dto.UpdateStoreParams;
import com.stallmart.product.dto.ProductDTO;
import com.stallmart.store.dto.StoreDTO;
import com.stallmart.store.StoreService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/stores")
public class StoreController {

    private final StoreService catalogService;

    public StoreController(StoreService catalogService) {
        this.catalogService = catalogService;
    }

    @GetMapping("/{id}")
    public Result<StoreDTO> getStore(@PathVariable long id) {
        return Result.success(catalogService.getStore(id));
    }

    @PutMapping("/{id}")
    public Result<StoreDTO> updateStore(@PathVariable long id, @RequestBody UpdateStoreParams request) {
        return Result.success(catalogService.updateStore(id, request));
    }

    @GetMapping("/qr/{qrCode}")
    public Result<StoreDTO> getStoreByQrCode(@PathVariable String qrCode) {
        return Result.success(catalogService.getStoreByQrCode(qrCode));
    }

    @GetMapping("/{storeId}/products")
    public Result<List<ProductDTO>> listProducts(@PathVariable long storeId) {
        return Result.success(catalogService.listProducts(storeId));
    }
}
