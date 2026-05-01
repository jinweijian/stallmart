package com.stallmart.product.internal.api;

import com.stallmart.support.web.Result;
import com.stallmart.product.dto.ProductDTO;
import com.stallmart.store.StoreService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final StoreService catalogService;

    public ProductController(StoreService catalogService) {
        this.catalogService = catalogService;
    }

    @GetMapping("/{id}")
    public Result<ProductDTO> getProduct(@PathVariable long id) {
        return Result.success(catalogService.getProduct(id));
    }
}
