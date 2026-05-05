package com.stallmart.store.internal.api;

import com.stallmart.store.StoreService;
import com.stallmart.store.dto.AppBootstrapDTO;
import com.stallmart.store.dto.StorefrontDTO;
import com.stallmart.support.web.Result;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/app")
public class AppBootstrapController {

    private final StoreService storeService;

    public AppBootstrapController(StoreService storeService) {
        this.storeService = storeService;
    }

    @GetMapping("/bootstrap")
    public Result<AppBootstrapDTO> bootstrap(
            @RequestParam(required = false) String appId,
            HttpServletRequest request
    ) {
        String resolvedAppId = appId == null || appId.isBlank() ? request.getHeader("X-App-Id") : appId;
        StorefrontDTO storefront = storeService.getStorefrontByAppId(resolvedAppId);
        return Result.success(new AppBootstrapDTO(
                storefront.id(),
                storefront.styleId(),
                storefront.styleCode(),
                storefront.decoration().style().version(),
                storefront
        ));
    }
}
