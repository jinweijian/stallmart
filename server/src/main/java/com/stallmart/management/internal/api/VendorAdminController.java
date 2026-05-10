package com.stallmart.management.internal.api;

import com.stallmart.cart.CartService;
import com.stallmart.cart.dto.CartDTO;
import com.stallmart.management.VendorAssetService;
import com.stallmart.management.VendorOrderCommandService;
import com.stallmart.management.VendorWorkspaceService;
import com.stallmart.management.dto.VendorWorkspaceDTO;
import com.stallmart.management.internal.security.AdminAccessGuard;
import com.stallmart.order.OrderService;
import com.stallmart.order.dto.OrderDTO;
import com.stallmart.product.dto.AssetDTO;
import com.stallmart.product.dto.CategoryDTO;
import com.stallmart.product.dto.CategoryUpsertParams;
import com.stallmart.product.dto.ProductDTO;
import com.stallmart.product.dto.ProductUpsertParams;
import com.stallmart.style.dto.SpecDTO;
import com.stallmart.style.dto.SpecUpsertParams;
import com.stallmart.store.StoreService;
import com.stallmart.store.dto.StoreDTO;
import com.stallmart.store.dto.StoreDecorationDTO;
import com.stallmart.store.dto.UpdateDecorationParams;
import com.stallmart.store.dto.UpdateStoreParams;
import com.stallmart.support.exception.AppException;
import com.stallmart.support.exception.ErrorCode;
import com.stallmart.support.web.Result;
import com.stallmart.user.dto.UserProfileDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.io.IOException;
import java.util.List;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/admin/vendor/me")
public class VendorAdminController {

    private final StoreService storeService;
    private final OrderService orderService;
    private final CartService cartService;
    private final VendorAssetService vendorAssetService;
    private final VendorOrderCommandService vendorOrderCommandService;
    private final VendorWorkspaceService vendorWorkspaceService;
    private final AdminAccessGuard accessGuard;

    public VendorAdminController(
            StoreService storeService,
            OrderService orderService,
            CartService cartService,
            VendorAssetService vendorAssetService,
            VendorOrderCommandService vendorOrderCommandService,
            VendorWorkspaceService vendorWorkspaceService,
            AdminAccessGuard accessGuard
    ) {
        this.storeService = storeService;
        this.orderService = orderService;
        this.cartService = cartService;
        this.vendorAssetService = vendorAssetService;
        this.vendorOrderCommandService = vendorOrderCommandService;
        this.vendorWorkspaceService = vendorWorkspaceService;
        this.accessGuard = accessGuard;
    }

    @GetMapping("/summary")
    public Result<VendorWorkspaceDTO> summary(HttpServletRequest request) {
        return Result.success(vendorWorkspaceService.buildVendorWorkspace(resolveStore(request)));
    }

    @GetMapping("/store")
    public Result<StoreDTO> store(HttpServletRequest request) {
        return Result.success(resolveStore(request));
    }

    @PutMapping("/store")
    public Result<StoreDTO> updateStore(@RequestBody UpdateStoreParams params, HttpServletRequest request) {
        StoreDTO store = resolveStore(request);
        return Result.success(storeService.updateStore(store.id(), params));
    }

    @GetMapping("/products")
    public Result<List<ProductDTO>> products(HttpServletRequest request) {
        return Result.success(storeService.listProducts(resolveStore(request).id()));
    }

    @GetMapping("/categories")
    public Result<List<CategoryDTO>> categories(
            @RequestParam(defaultValue = "PRODUCT") String module,
            HttpServletRequest request
    ) {
        return Result.success(storeService.listCategories(resolveStore(request).id(), module));
    }

    @PostMapping("/categories")
    public Result<CategoryDTO> createCategory(
            @Valid @RequestBody CategoryUpsertParams params,
            HttpServletRequest request
    ) {
        return Result.success(storeService.createCategory(resolveStore(request).id(), params));
    }

    @PutMapping("/categories/{categoryId}")
    public Result<CategoryDTO> updateCategory(
            @PathVariable long categoryId,
            @Valid @RequestBody CategoryUpsertParams params,
            HttpServletRequest request
    ) {
        return Result.success(storeService.updateCategory(resolveStore(request).id(), categoryId, params));
    }

    @PostMapping("/assets/product-image")
    public Result<AssetDTO> uploadProductImage(
            @RequestParam("file") MultipartFile file,
            HttpServletRequest request
    ) throws IOException {
        return uploadAsset(file, request, "products");
    }

    @PostMapping("/assets/decoration-image")
    public Result<AssetDTO> uploadDecorationImage(
            @RequestParam("file") MultipartFile file,
            HttpServletRequest request
    ) throws IOException {
        return uploadAsset(file, request, "decoration");
    }

    private Result<AssetDTO> uploadAsset(
            MultipartFile file,
            HttpServletRequest request,
            String folder
    ) throws IOException {
        return Result.success(vendorAssetService.upload(resolveStore(request), file, folder));
    }

    @PostMapping("/products")
    public Result<ProductDTO> createProduct(@Valid @RequestBody ProductUpsertParams params, HttpServletRequest request) {
        return Result.success(storeService.createProduct(resolveStore(request).id(), params));
    }

    @PutMapping("/products/{productId}")
    public Result<ProductDTO> updateProduct(
            @PathVariable long productId,
            @Valid @RequestBody ProductUpsertParams params,
            HttpServletRequest request
    ) {
        return Result.success(storeService.updateProduct(resolveStore(request).id(), productId, params));
    }

    @GetMapping("/products/{productId}")
    public Result<ProductDTO> product(@PathVariable long productId, HttpServletRequest request) {
        StoreDTO store = resolveStore(request);
        ProductDTO product = storeService.getProduct(productId);
        accessGuard.requireProductInStore(store, product);
        return Result.success(product);
    }

    @PutMapping("/products/{productId}/on-sale")
    public Result<ProductDTO> onSale(@PathVariable long productId, HttpServletRequest request) {
        return Result.success(storeService.updateProductStatus(resolveStore(request).id(), productId, "ACTIVE"));
    }

    @PutMapping("/products/{productId}/off-sale")
    public Result<ProductDTO> offSale(@PathVariable long productId, HttpServletRequest request) {
        return Result.success(storeService.updateProductStatus(resolveStore(request).id(), productId, "INACTIVE"));
    }

    @PutMapping("/products/{productId}/sold-out")
    public Result<ProductDTO> soldOut(@PathVariable long productId, HttpServletRequest request) {
        return Result.success(storeService.updateProductStatus(resolveStore(request).id(), productId, "SOLD_OUT"));
    }

    @GetMapping("/orders")
    public Result<List<OrderDTO>> orders(HttpServletRequest request) {
        return Result.success(orderService.listByStore(resolveStore(request).id()));
    }

    @PutMapping("/orders/{orderId}/{action}")
    public Result<OrderDTO> transitionOrder(@PathVariable long orderId, @PathVariable String action, HttpServletRequest request) {
        return Result.success(vendorOrderCommandService.transition(resolveStore(request), orderId, action));
    }

    @GetMapping("/orders/{orderId}")
    public Result<OrderDTO> order(@PathVariable long orderId, HttpServletRequest request) {
        StoreDTO store = resolveStore(request);
        OrderDTO order = orderService.getAdmin(orderId);
        accessGuard.requireOrderInStore(store, order);
        return Result.success(order);
    }

    @GetMapping("/carts")
    public Result<List<CartDTO>> carts(HttpServletRequest request) {
        return Result.success(cartService.listByStore(resolveStore(request).id()));
    }

    @GetMapping("/users")
    public Result<List<UserProfileDTO>> users(HttpServletRequest request) {
        return Result.success(vendorWorkspaceService.listUsersForStore(resolveStore(request).id()));
    }

    @GetMapping("/users/{userId}/orders")
    public Result<List<OrderDTO>> userOrders(@PathVariable long userId, HttpServletRequest request) {
        StoreDTO store = resolveStore(request);
        boolean userBelongsToStore = vendorWorkspaceService.listUsersForStore(store.id()).stream()
                .anyMatch(user -> user.id().equals(userId));
        if (!userBelongsToStore) {
            throw new AppException(ErrorCode.NOT_FOUND);
        }
        return Result.success(orderService.listByStoreAndUser(store.id(), userId));
    }

    @GetMapping("/decoration")
    public Result<StoreDecorationDTO> decoration(HttpServletRequest request) {
        return Result.success(storeService.getDecoration(resolveStore(request).id()));
    }

    @PutMapping("/decoration")
    public Result<StoreDecorationDTO> updateDecoration(
            @RequestBody UpdateDecorationParams params,
            HttpServletRequest request
    ) {
        return Result.success(storeService.updateDecoration(resolveStore(request).id(), params));
    }

    @GetMapping("/specs")
    public Result<List<SpecDTO>> specs(HttpServletRequest request) {
        StoreDTO store = resolveStore(request);
        return Result.success(storeService.listSpecs(store.styleId()));
    }

    @PostMapping("/specs")
    public Result<SpecDTO> createSpec(@Valid @RequestBody SpecUpsertParams params, HttpServletRequest request) {
        StoreDTO store = resolveStore(request);
        return Result.success(storeService.createSpec(store.styleId(), params));
    }

    @PutMapping("/specs/{specId}")
    public Result<SpecDTO> updateSpec(
            @PathVariable long specId,
            @Valid @RequestBody SpecUpsertParams params,
            HttpServletRequest request
    ) {
        StoreDTO store = resolveStore(request);
        return Result.success(storeService.updateSpec(store.styleId(), specId, params));
    }

    @DeleteMapping("/specs/{specId}")
    public Result<Void> deleteSpec(@PathVariable long specId, HttpServletRequest request) {
        StoreDTO store = resolveStore(request);
        storeService.deleteSpec(store.styleId(), specId);
        return Result.success(null);
    }

    private StoreDTO resolveStore(HttpServletRequest request) {
        return accessGuard.requireVendorStore(request);
    }

}
