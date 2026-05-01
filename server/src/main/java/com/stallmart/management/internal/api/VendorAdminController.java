package com.stallmart.management.internal.api;

import com.stallmart.cart.CartService;
import com.stallmart.cart.dto.CartDTO;
import com.stallmart.management.dto.VendorWorkspaceDTO;
import com.stallmart.order.OrderService;
import com.stallmart.order.dto.OrderDTO;
import com.stallmart.product.dto.ProductDTO;
import com.stallmart.product.dto.ProductUpsertParams;
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
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/vendor/me")
public class VendorAdminController {

    private final StoreService storeService;
    private final OrderService orderService;
    private final CartService cartService;
    private final UserService userService;
    private final CurrentUserResolver currentUserResolver;

    public VendorAdminController(
            StoreService storeService,
            OrderService orderService,
            CartService cartService,
            UserService userService,
            CurrentUserResolver currentUserResolver
    ) {
        this.storeService = storeService;
        this.orderService = orderService;
        this.cartService = cartService;
        this.userService = userService;
        this.currentUserResolver = currentUserResolver;
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

    @GetMapping("/orders")
    public Result<List<OrderDTO>> orders(HttpServletRequest request) {
        return Result.success(orderService.listByStore(resolveStore(request).id()));
    }

    @PutMapping("/orders/{orderId}/{action}")
    public Result<OrderDTO> transitionOrder(@PathVariable long orderId, @PathVariable String action) {
        return switch (action) {
            case "accept" -> Result.success(orderService.accept(orderId));
            case "reject" -> Result.success(orderService.reject(orderId));
            case "prepare" -> Result.success(orderService.prepare(orderId));
            case "ready" -> Result.success(orderService.ready(orderId));
            case "complete" -> Result.success(orderService.complete(orderId));
            default -> throw new AppException(ErrorCode.BAD_REQUEST);
        };
    }

    @GetMapping("/carts")
    public Result<List<CartDTO>> carts(HttpServletRequest request) {
        return Result.success(cartService.listByStore(resolveStore(request).id()));
    }

    @GetMapping("/users")
    public Result<List<UserProfileDTO>> users(HttpServletRequest request) {
        return Result.success(usersForStore(resolveStore(request).id()));
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
        long ownerId = currentUserResolver.resolve(request);
        return storeService.listStoresByOwner(ownerId).stream()
                .findFirst()
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));
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
}
