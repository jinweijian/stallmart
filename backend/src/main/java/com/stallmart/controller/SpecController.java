package com.stallmart.controller;

import com.stallmart.common.result.Result;
import com.stallmart.model.entity.ProductSpec;
import com.stallmart.model.vo.ProductSpecVO;
import com.stallmart.service.ProductSpecService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "规格", description = "规格管理")
@RestController
@RequestMapping("/api/v1/styles/{styleId}/specs")
@RequiredArgsConstructor
public class SpecController {

    private final ProductSpecService productSpecService;

    @Operation(summary = "获取风格规格列表")
    @GetMapping
    public Result<List<ProductSpecVO>> getSpecs(@PathVariable Long styleId) {
        List<ProductSpec> specs = productSpecService.findByStyleId(styleId);
        List<ProductSpecVO> voList = specs.stream()
                .map(this::toVO)
                .collect(Collectors.toList());
        return Result.success(voList);
    }

    private ProductSpecVO toVO(ProductSpec spec) {
        return ProductSpecVO.builder()
                .id(spec.getId())
                .styleId(spec.getStyleId())
                .name(spec.getName())
                .description(spec.getDescription())
                .sortOrder(spec.getSortOrder())
                .build();
    }
}
