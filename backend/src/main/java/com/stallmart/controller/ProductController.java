package com.stallmart.controller;

import com.stallmart.common.result.Result;
import com.stallmart.model.entity.Product;
import com.stallmart.model.vo.ProductVO;
import com.stallmart.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 商品控制器
 */
@Tag(name = "商品", description = "商品列表、商品详情")
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @Operation(summary = "获取店铺商品列表")
    @GetMapping("/stores/{storeId}/products")
    public Result<List<ProductVO>> getProducts(@PathVariable Long storeId) {
        List<Product> products = productService.findByStoreId(storeId);
        List<ProductVO> voList = products.stream()
                .map(this::toProductVO)
                .collect(Collectors.toList());
        return Result.success(voList);
    }

    private ProductVO toProductVO(Product product) {
        return ProductVO.builder()
                .id(product.getId())
                .storeId(product.getStoreId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .imageUrl(product.getImageUrl())
                .specIds(product.getSpecIds())
                .status(product.getStatus())
                .sortOrder(product.getSortOrder())
                .build();
    }
}
