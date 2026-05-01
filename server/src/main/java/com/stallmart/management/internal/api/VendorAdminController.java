package com.stallmart.management.internal.api;

import com.stallmart.cart.CartService;
import com.stallmart.cart.dto.CartDTO;
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
import com.stallmart.user.UserService;
import com.stallmart.user.dto.UserProfileDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
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

    private static final long MAX_PRODUCT_IMAGE_SIZE = 10L * 1024L * 1024L;

    private final StoreService storeService;
    private final OrderService orderService;
    private final CartService cartService;
    private final UserService userService;
    private final AdminAccessGuard accessGuard;

    public VendorAdminController(
            StoreService storeService,
            OrderService orderService,
            CartService cartService,
            UserService userService,
            AdminAccessGuard accessGuard
    ) {
        this.storeService = storeService;
        this.orderService = orderService;
        this.cartService = cartService;
        this.userService = userService;
        this.accessGuard = accessGuard;
    }

    @GetMapping("/summary")
    public Result<VendorWorkspaceDTO> summary(HttpServletRequest request) {
        return Result.success(buildWorkspace(resolveStore(request)));
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
        StoreDTO store = resolveStore(request);
        String contentType = file.getContentType() == null ? "" : file.getContentType();
        if (file.isEmpty() || file.getSize() > MAX_PRODUCT_IMAGE_SIZE || !contentType.startsWith("image/")) {
            throw new AppException(ErrorCode.BAD_REQUEST);
        }
        String extension = extensionOf(file.getOriginalFilename(), contentType);
        String filename = UUID.randomUUID() + extension;
        Path directory = Path.of("uploads", "stores", String.valueOf(store.id()), folder).toAbsolutePath().normalize();
        Files.createDirectories(directory);
        Path target = directory.resolve(filename);
        file.transferTo(target);
        return Result.success(new AssetDTO("/uploads/stores/" + store.id() + "/" + folder + "/" + filename, filename, file.getSize()));
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
        StoreDTO store = resolveStore(request);
        accessGuard.requireOrderInStore(store, orderService.getAdmin(orderId));
        return switch (action) {
            case "accept" -> Result.success(orderService.accept(orderId));
            case "reject" -> Result.success(orderService.reject(orderId));
            case "prepare" -> Result.success(orderService.prepare(orderId));
            case "ready" -> Result.success(orderService.ready(orderId));
            case "complete" -> Result.success(orderService.complete(orderId));
            default -> throw new AppException(ErrorCode.BAD_REQUEST);
        };
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
        return Result.success(usersForStore(resolveStore(request).id()));
    }

    @GetMapping("/users/{userId}/orders")
    public Result<List<OrderDTO>> userOrders(@PathVariable long userId, HttpServletRequest request) {
        StoreDTO store = resolveStore(request);
        boolean userBelongsToStore = usersForStore(store.id()).stream()
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

    private VendorWorkspaceDTO buildWorkspace(StoreDTO store) {
        List<OrderDTO> orders = orderService.listByStore(store.id());
        List<CartDTO> carts = cartService.listByStore(store.id());
        return new VendorWorkspaceDTO(
                store,
                storeService.getDecoration(store.id()),
                storeService.listProducts(store.id()),
                orders,
                carts,
                usersForStore(store.id()),
                storeService.listStyles(),
                orders.size(),
                carts.size(),
                salesAmount(orders)
        );
    }

    private StoreDTO resolveStore(HttpServletRequest request) {
        return accessGuard.requireVendorStore(request);
    }

    private List<UserProfileDTO> usersForStore(long storeId) {
        Set<Long> userIds = orderService.listByStore(storeId).stream()
                .map(OrderDTO::userId)
                .collect(Collectors.toSet());
        cartService.listByStore(storeId).stream()
                .map(CartDTO::userId)
                .forEach(userIds::add);
        return userService.listUsers().stream()
                .filter(user -> userIds.contains(user.id()))
                .toList();
    }

    private BigDecimal salesAmount(List<OrderDTO> orders) {
        return orders.stream()
                .filter(order -> !order.status().equals("REJECTED"))
                .map(OrderDTO::totalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private String extensionOf(String filename, String contentType) {
        if (filename != null && filename.contains(".")) {
            String extension = filename.substring(filename.lastIndexOf(".")).toLowerCase();
            if (extension.matches("\\.(png|jpg|jpeg|gif|webp)")) {
                return extension;
            }
        }
        return switch (contentType) {
            case "image/png" -> ".png";
            case "image/gif" -> ".gif";
            case "image/webp" -> ".webp";
            default -> ".jpg";
        };
    }
}
