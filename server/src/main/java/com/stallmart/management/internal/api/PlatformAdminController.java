package com.stallmart.management.internal.api;

import com.stallmart.cart.CartService;
import com.stallmart.cart.dto.CartDTO;
import com.stallmart.management.dto.AdminSummaryDTO;
import com.stallmart.management.dto.VendorWorkspaceDTO;
import com.stallmart.management.internal.security.AdminAccessGuard;
import com.stallmart.order.OrderService;
import com.stallmart.order.dto.OrderDTO;
import com.stallmart.product.dto.ProductDTO;
import com.stallmart.store.StoreService;
import com.stallmart.store.dto.StoreDTO;
import com.stallmart.support.web.Result;
import com.stallmart.user.UserService;
import com.stallmart.user.dto.UserProfileDTO;
import jakarta.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/platform")
public class PlatformAdminController {

    private final StoreService storeService;
    private final OrderService orderService;
    private final CartService cartService;
    private final UserService userService;
    private final AdminAccessGuard accessGuard;

    public PlatformAdminController(
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
    public Result<AdminSummaryDTO> summary(HttpServletRequest request) {
        accessGuard.requirePlatformAdmin(request);
        List<OrderDTO> orders = orderService.listAll();
        return Result.success(new AdminSummaryDTO(
                storeService.listStores().size(),
                userService.listUsers().size(),
                orders.size(),
                cartService.listAll().size(),
                salesAmount(orders)
        ));
    }

    @GetMapping("/vendors")
    public Result<List<StoreDTO>> vendors(HttpServletRequest request) {
        accessGuard.requirePlatformAdmin(request);
        return Result.success(storeService.listStores());
    }

    @GetMapping("/vendors/{storeId}/summary")
    public Result<VendorWorkspaceDTO> vendorSummary(@PathVariable long storeId, HttpServletRequest request) {
        accessGuard.requirePlatformAdmin(request);
        return Result.success(buildWorkspace(storeId));
    }

    @GetMapping("/vendors/{storeId}/products")
    public Result<List<ProductDTO>> products(@PathVariable long storeId, HttpServletRequest request) {
        accessGuard.requirePlatformAdmin(request);
        return Result.success(storeService.listProducts(storeId));
    }

    @GetMapping("/vendors/{storeId}/orders")
    public Result<List<OrderDTO>> orders(@PathVariable long storeId, HttpServletRequest request) {
        accessGuard.requirePlatformAdmin(request);
        return Result.success(orderService.listByStore(storeId));
    }

    @GetMapping("/vendors/{storeId}/carts")
    public Result<List<CartDTO>> carts(@PathVariable long storeId, HttpServletRequest request) {
        accessGuard.requirePlatformAdmin(request);
        return Result.success(cartService.listByStore(storeId));
    }

    @GetMapping("/vendors/{storeId}/users")
    public Result<List<UserProfileDTO>> users(@PathVariable long storeId, HttpServletRequest request) {
        accessGuard.requirePlatformAdmin(request);
        return Result.success(usersForStore(storeId));
    }

    private VendorWorkspaceDTO buildWorkspace(long storeId) {
        StoreDTO store = storeService.getStore(storeId);
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

    private List<UserProfileDTO> usersForStore(long storeId) {
        Set<Long> userIds = orderService.listByStore(storeId).stream()
                .map(OrderDTO::userId)
                .collect(Collectors.toSet());
        cartService.listByStore(storeId).stream()
                .map(CartDTO::userId)
                .forEach(userIds::add);
        return userService.listUsers().stream()
                .filter(user -> userIds.contains(user.id()) || user.role().equals("ADMIN"))
                .toList();
    }

    private BigDecimal salesAmount(List<OrderDTO> orders) {
        return orders.stream()
                .filter(order -> !order.status().equals("REJECTED"))
                .map(OrderDTO::totalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
