package com.stallmart.management.internal.api;

import com.stallmart.cart.CartService;
import com.stallmart.cart.dto.CartDTO;
import com.stallmart.management.OperationLogService;
import com.stallmart.management.dto.AdminSummaryDTO;
import com.stallmart.management.dto.OperationLogDTO;
import com.stallmart.management.dto.OperationLogRecordParams;
import com.stallmart.management.dto.VendorWorkspaceDTO;
import com.stallmart.management.internal.model.OperationLogResult;
import com.stallmart.management.internal.model.OperationLogScope;
import com.stallmart.management.internal.security.AdminAccessGuard;
import com.stallmart.order.OrderService;
import com.stallmart.order.dto.OrderDTO;
import com.stallmart.order.internal.model.OrderStatus;
import com.stallmart.product.dto.ProductDTO;
import com.stallmart.store.StoreService;
import com.stallmart.store.dto.StoreDTO;
import com.stallmart.store.internal.model.StoreStyleStatus;
import com.stallmart.style.dto.StyleDTO;
import com.stallmart.style.dto.StyleUpsertParams;
import com.stallmart.support.security.CurrentUserResolver;
import com.stallmart.support.web.Result;
import com.stallmart.user.UserService;
import com.stallmart.user.dto.UserProfileDTO;
import com.stallmart.user.internal.model.UserRole;
import com.stallmart.user.internal.repository.AdminAccountRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
    private final OperationLogService operationLogService;
    private final CurrentUserResolver currentUserResolver;
    private final AdminAccountRepository adminAccountRepository;

    public PlatformAdminController(
            StoreService storeService,
            OrderService orderService,
            CartService cartService,
            UserService userService,
            AdminAccessGuard accessGuard,
            OperationLogService operationLogService,
            CurrentUserResolver currentUserResolver,
            AdminAccountRepository adminAccountRepository
    ) {
        this.storeService = storeService;
        this.orderService = orderService;
        this.cartService = cartService;
        this.userService = userService;
        this.accessGuard = accessGuard;
        this.operationLogService = operationLogService;
        this.currentUserResolver = currentUserResolver;
        this.adminAccountRepository = adminAccountRepository;
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

    @GetMapping("/styles")
    public Result<List<StyleDTO>> styles(HttpServletRequest request) {
        accessGuard.requirePlatformAdmin(request);
        return Result.success(storeService.listStyles());
    }

    @GetMapping("/operation-logs")
    public Result<List<OperationLogDTO>> operationLogs(HttpServletRequest request) {
        accessGuard.requirePlatformAdmin(request);
        return Result.success(operationLogService.listPlatformLogs());
    }

    @GetMapping("/styles/{styleId}")
    public Result<StyleDTO> style(@PathVariable long styleId, HttpServletRequest request) {
        accessGuard.requirePlatformAdmin(request);
        return Result.success(storeService.getStyle(styleId));
    }

    @PostMapping("/styles")
    public Result<StyleDTO> createStyle(
            @Valid @RequestBody StyleUpsertParams params,
            HttpServletRequest request
    ) {
        accessGuard.requirePlatformAdmin(request);
        StyleDTO style = storeService.createStyle(params);
        recordPlatformLog(request, "STYLE_CREATE", "STYLE", String.valueOf(style.id()), "新增风格包：" + style.name());
        return Result.success(style);
    }

    @PutMapping("/styles/{styleId}")
    public Result<StyleDTO> updateStyle(
            @PathVariable long styleId,
            @Valid @RequestBody StyleUpsertParams params,
            HttpServletRequest request
    ) {
        accessGuard.requirePlatformAdmin(request);
        StyleDTO style = storeService.updateStyle(styleId, params);
        recordPlatformLog(request, "STYLE_UPDATE", "STYLE", String.valueOf(style.id()), "更新风格包：" + style.name());
        return Result.success(style);
    }

    @PutMapping("/styles/{styleId}/publish")
    public Result<StyleDTO> publishStyle(@PathVariable long styleId, HttpServletRequest request) {
        accessGuard.requirePlatformAdmin(request);
        StyleDTO style = storeService.updateStyleStatus(styleId, StoreStyleStatus.ACTIVE);
        recordPlatformLog(request, "STYLE_PUBLISH", "STYLE", String.valueOf(style.id()), "上架风格包：" + style.name());
        return Result.success(style);
    }

    @PutMapping("/styles/{styleId}/unpublish")
    public Result<StyleDTO> unpublishStyle(@PathVariable long styleId, HttpServletRequest request) {
        accessGuard.requirePlatformAdmin(request);
        StyleDTO style = storeService.updateStyleStatus(styleId, StoreStyleStatus.INACTIVE);
        recordPlatformLog(request, "STYLE_UNPUBLISH", "STYLE", String.valueOf(style.id()), "下架风格包：" + style.name());
        return Result.success(style);
    }

    @DeleteMapping("/styles/{styleId}")
    public Result<Void> deleteStyle(@PathVariable long styleId, HttpServletRequest request) {
        accessGuard.requirePlatformAdmin(request);
        storeService.deleteStyle(styleId);
        recordPlatformLog(request, "STYLE_DELETE", "STYLE", String.valueOf(styleId), "删除风格包：" + styleId);
        return Result.success(null);
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

    @GetMapping("/vendors/{storeId}/operation-logs")
    public Result<List<OperationLogDTO>> vendorOperationLogs(@PathVariable long storeId, HttpServletRequest request) {
        accessGuard.requirePlatformAdmin(request);
        return Result.success(operationLogService.listVendorLogs(storeId));
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
                .filter(user -> userIds.contains(user.id()) || user.role() == UserRole.ADMIN)
                .toList();
    }

    private BigDecimal salesAmount(List<OrderDTO> orders) {
        return orders.stream()
                .filter(order -> order.status() != OrderStatus.REJECTED)
                .map(OrderDTO::totalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private void recordPlatformLog(
            HttpServletRequest request,
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
                OperationLogScope.PLATFORM,
                null,
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
