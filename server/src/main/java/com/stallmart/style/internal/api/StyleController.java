package com.stallmart.style.internal.api;

import com.stallmart.support.web.Result;
import com.stallmart.style.dto.SpecDTO;
import com.stallmart.style.dto.StyleDTO;
import com.stallmart.store.StoreService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/styles")
public class StyleController {

    private final StoreService catalogService;

    public StyleController(StoreService catalogService) {
        this.catalogService = catalogService;
    }

    @GetMapping
    public Result<List<StyleDTO>> listStyles() {
        return Result.success(catalogService.listStyles());
    }

    @GetMapping("/{id}")
    public Result<StyleDTO> getStyle(@PathVariable long id) {
        return Result.success(catalogService.getStyle(id));
    }

    @GetMapping("/{styleId}/specs")
    public Result<List<SpecDTO>> listSpecs(@PathVariable long styleId) {
        return Result.success(catalogService.listSpecs(styleId));
    }
}
