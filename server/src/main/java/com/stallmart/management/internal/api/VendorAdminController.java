package com.stallmart.management.internal.api;

import com.stallmart.cart.CartService;
import com.stallmart.cart.dto.CartDTO;
import com.stallmart.management.VendorAssetService;
import com.stallmart.management.VendorOrderCommandService;
import com.stallmart.management.VendorWorkspaceService;
import com.stallmart.management.OperationLogService;
import com.stallmart.management.dto.OperationLogDTO;
import com.stallmart.management.dto.OperationLogRecordParams;
import com.stallmart.management.dto.VendorWorkspaceDTO;
import com.stallmart.management.internal.model.OperationLogResult;
import com.stallmart.management.internal.model.OperationLogScope;
import com.stallmart.management.internal.security.AdminAccessGuard;
import com.stallmart.order.OrderService;
import com.stallmart.order.dto.OrderDTO;
import com.stallmart.product.dto.AssetDTO;
import com.stallmart.product.dto.CategoryDTO;
import com.stallmart.product.dto.CategoryUpsertParams;
import com.stallmart.product.dto.ProductDTO;
import com.stallmart.product.dto.ProductUpsertParams;
import com.stallmart.store.internal.model.ProductStatus;
import com.stallmart.style.dto.SpecDTO;
import com.stallmart.style.dto.SpecUpsertParams;
import com.stallmart.store.StoreService;
import com.stallmart.store.dto.StoreDTO;
import com.stallmart.store.dto.StoreDecorationDTO;
import com.stallmart.store.dto.UpdateDecorationParams;
import com.stallmart.store.dto.UpdateStoreParams;
import com.stallmart.support.exception.AppException;
import com.stallmart.support.exception.ErrorCode;
import com.stallmart.support.security.CurrentUserResolver;
import com.stallmart.support.web.Result;
import com.stallmart.user.UserService;
import com.stallmart.user.dto.UserProfileDTO;
import com.stallmart.user.internal.repository.AdminAccountRepository;
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
    private final OperationLogService operationLogService;
    private final CurrentUserResolver currentUserResolver;
    private final UserService userService;
    private final AdminAccountRepository adminAccountRepository;

    public VendorAdminController(
            StoreService storeService,
            OrderService orderService,
            CartService cartService,
            VendorAssetService vendorAssetService,
            VendorOrderCommandService vendorOrderCommandService,
            VendorWorkspaceService vendorWorkspaceService,
            AdminAccessGuard accessGuard,
            OperationLogService operationLogService,
            CurrentUserResolver currentUserResolver,
            UserService userService,
            AdminAccountRepository adminAccountRepository
    ) {
        this.storeService = storeService;
        this.orderService = orderService;
        this.cartService = cartService;
        this.vendorAssetService = vendorAssetService;
        this.vendorOrderCommandService = vendorOrderCommandService;
        this.vendorWorkspaceService = vendorWorkspaceService;
        this.accessGuard = accessGuard;
        this.operationLogService = operationLogService;
        this.currentUserResolver = currentUserResolver;
        this.userService = userService;
        this.adminAccountRepository = adminAccountRepository;
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
        StoreDTO updated = storeService.updateStore(store.id(), params);
        recordVendorLog(request, store, "STORE_UPDATE", "STORE", String.valueOf(store.id()), "更新店铺资料：" + updated.name());
        return Result.success(updated);
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
        StoreDTO store = resolveStore(request);
        CategoryDTO category = storeService.createCategory(store.id(), params);
        recordVendorLog(request, store, "CATEGORY_CREATE", "CATEGORY", String.valueOf(category.id()), "新增分类：" + category.name());
        return Result.success(category);
    }

    @PutMapping("/categories/{categoryId}")
    public Result<CategoryDTO> updateCategory(
            @PathVariable long categoryId,
            @Valid @RequestBody CategoryUpsertParams params,
            HttpServletRequest request
    ) {
        StoreDTO store = resolveStore(request);
        CategoryDTO category = storeService.updateCategory(store.id(), categoryId, params);
        recordVendorLog(request, store, "CATEGORY_UPDATE", "CATEGORY", String.valueOf(category.id()), "更新分类：" + category.name());
        return Result.success(category);
    }

    @PostMapping("/assets/product-image")
    public Result<AssetDTO> uploadProductImage(
            @RequestParam("file") MultipartFile file,
            HttpServletRequest request
    ) throws IOException {
        StoreDTO store = resolveStore(request);
        AssetDTO asset = vendorAssetService.upload(store, file, "products");
        recordVendorLog(request, store, "ASSET_UPLOAD", "ASSET", asset.filename(), "上传商品图片：" + asset.filename());
        return Result.success(asset);
    }

    @PostMapping("/assets/decoration-image")
    public Result<AssetDTO> uploadDecorationImage(
            @RequestParam("file") MultipartFile file,
            HttpServletRequest request
    ) throws IOException {
        StoreDTO store = resolveStore(request);
        AssetDTO asset = vendorAssetService.upload(store, file, "decoration");
        recordVendorLog(request, store, "ASSET_UPLOAD", "ASSET", asset.filename(), "上传装修图片：" + asset.filename());
        return Result.success(asset);
    }

    @PostMapping("/products")
    public Result<ProductDTO> createProduct(@Valid @RequestBody ProductUpsertParams params, HttpServletRequest request) {
        StoreDTO store = resolveStore(request);
        ProductDTO product = storeService.createProduct(store.id(), params);
        recordVendorLog(request, store, "PRODUCT_CREATE", "PRODUCT", String.valueOf(product.id()), "新增商品：" + product.name());
        return Result.success(product);
    }

    @PutMapping("/products/{productId}")
    public Result<ProductDTO> updateProduct(
            @PathVariable long productId,
            @Valid @RequestBody ProductUpsertParams params,
            HttpServletRequest request
    ) {
        StoreDTO store = resolveStore(request);
        ProductDTO product = storeService.updateProduct(store.id(), productId, params);
        recordVendorLog(request, store, "PRODUCT_UPDATE", "PRODUCT", String.valueOf(product.id()), "更新商品：" + product.name());
        return Result.success(product);
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
        return updateProductStatus(productId, ProductStatus.ACTIVE, "PRODUCT_ON_SALE", "商品上架", request);
    }

    @PutMapping("/products/{productId}/off-sale")
    public Result<ProductDTO> offSale(@PathVariable long productId, HttpServletRequest request) {
        return updateProductStatus(productId, ProductStatus.INACTIVE, "PRODUCT_OFF_SALE", "商品下架", request);
    }

    @PutMapping("/products/{productId}/sold-out")
    public Result<ProductDTO> soldOut(@PathVariable long productId, HttpServletRequest request) {
        return updateProductStatus(productId, ProductStatus.SOLD_OUT, "PRODUCT_SOLD_OUT", "商品售罄", request);
    }

    @GetMapping("/orders")
    public Result<List<OrderDTO>> orders(HttpServletRequest request) {
        return Result.success(orderService.listByStore(resolveStore(request).id()));
    }

    @PutMapping("/orders/{orderId}/{action}")
    public Result<OrderDTO> transitionOrder(@PathVariable long orderId, @PathVariable String action, HttpServletRequest request) {
        StoreDTO store = resolveStore(request);
        OrderDTO order = vendorOrderCommandService.transition(store, orderId, action);
        recordVendorLog(request, store, "ORDER_TRANSITION", "ORDER", String.valueOf(order.id()), "订单流转：" + action);
        return Result.success(order);
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

    @GetMapping("/operation-logs")
    public Result<List<OperationLogDTO>> operationLogs(HttpServletRequest request) {
        return Result.success(operationLogService.listVendorLogs(resolveStore(request).id()));
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
        StoreDTO store = resolveStore(request);
        StoreDecorationDTO decoration = storeService.updateDecoration(store.id(), params);
        recordVendorLog(request, store, "DECORATION_UPDATE", "DECORATION", String.valueOf(store.id()), "更新店铺装修");
        return Result.success(decoration);
    }

    @GetMapping("/specs")
    public Result<List<SpecDTO>> specs(HttpServletRequest request) {
        StoreDTO store = resolveStore(request);
        return Result.success(storeService.listSpecs(store.styleId()));
    }

    @PostMapping("/specs")
    public Result<SpecDTO> createSpec(@Valid @RequestBody SpecUpsertParams params, HttpServletRequest request) {
        StoreDTO store = resolveStore(request);
        SpecDTO spec = storeService.createSpec(store.styleId(), params);
        recordVendorLog(request, store, "SPEC_CREATE", "SPEC", String.valueOf(spec.id()), "新增规格：" + spec.name());
        return Result.success(spec);
    }

    @PutMapping("/specs/{specId}")
    public Result<SpecDTO> updateSpec(
            @PathVariable long specId,
            @Valid @RequestBody SpecUpsertParams params,
            HttpServletRequest request
    ) {
        StoreDTO store = resolveStore(request);
        SpecDTO spec = storeService.updateSpec(store.styleId(), specId, params);
        recordVendorLog(request, store, "SPEC_UPDATE", "SPEC", String.valueOf(spec.id()), "更新规格：" + spec.name());
        return Result.success(spec);
    }

    @DeleteMapping("/specs/{specId}")
    public Result<Void> deleteSpec(@PathVariable long specId, HttpServletRequest request) {
        StoreDTO store = resolveStore(request);
        storeService.deleteSpec(store.styleId(), specId);
        recordVendorLog(request, store, "SPEC_DELETE", "SPEC", String.valueOf(specId), "删除规格：" + specId);
        return Result.success(null);
    }

    private Result<ProductDTO> updateProductStatus(
            long productId,
            ProductStatus status,
            String action,
            String description,
            HttpServletRequest request
    ) {
        StoreDTO store = resolveStore(request);
        ProductDTO product = storeService.updateProductStatus(store.id(), productId, status);
        recordVendorLog(request, store, action, "PRODUCT", String.valueOf(product.id()), description + "：" + product.name());
        return Result.success(product);
    }

    private StoreDTO resolveStore(HttpServletRequest request) {
        return accessGuard.requireVendorStore(request);
    }

    private void recordVendorLog(
            HttpServletRequest request,
            StoreDTO store,
            String action,
            String resourceType,
            String resourceId,
            String description
    ) {
        UserProfileDTO actor = userService.getProfile(currentUserResolver.resolve(request));
        String account = adminAccountRepository.findByUserId(actor.id())
                .map(adminAccount -> adminAccount.account)
                .orElse(actor.role().name().toLowerCase());
        operationLogService.record(new OperationLogRecordParams(
                OperationLogScope.VENDOR,
                store.id(),
                actor.id(),
                account,
                actor.role(),
                action,
                resourceType,
                resourceId,
                description,
                OperationLogResult.SUCCESS,
                clientIp(request),
                request.getHeader("User-Agent")
        ));
    }

    private String clientIp(HttpServletRequest request) {
        String forwardedFor = request.getHeader("X-Forwarded-For");
        if (forwardedFor != null && !forwardedFor.isBlank()) {
            return forwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

}
