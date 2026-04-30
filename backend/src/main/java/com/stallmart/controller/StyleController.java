package com.stallmart.controller;

import com.stallmart.common.result.Result;
import com.stallmart.model.entity.StoreStyle;
import com.stallmart.model.vo.StoreStyleVO;
import com.stallmart.service.StoreStyleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "风格包", description = "风格包列表、风格包详情")
@RestController
@RequestMapping("/api/v1/styles")
@RequiredArgsConstructor
public class StyleController {

    private final StoreStyleService storeStyleService;

    @Operation(summary = "获取风格包列表")
    @GetMapping
    public Result<List<StoreStyleVO>> getStyles() {
        List<StoreStyle> styles = storeStyleService.findAll();
        List<StoreStyleVO> voList = styles.stream()
                .map(this::toVO)
                .collect(Collectors.toList());
        return Result.success(voList);
    }

    @Operation(summary = "获取风格包详情")
    @GetMapping("/{id}")
    public Result<StoreStyleVO> getStyle(@PathVariable Long id) {
        StoreStyle style = storeStyleService.findById(id);
        return Result.success(toVO(style));
    }

    private StoreStyleVO toVO(StoreStyle style) {
        return StoreStyleVO.builder()
                .id(style.getId())
                .name(style.getName())
                .theme(style.getTheme())
                .config(style.getConfig())
                .build();
    }
}
