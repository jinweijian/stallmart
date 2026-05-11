package com.stallmart.management.internal.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.stallmart.cart.CartService;
import com.stallmart.cart.dto.CartDTO;
import com.stallmart.cart.internal.model.CartStatus;
import com.stallmart.management.dto.VendorWorkspaceDTO;
import com.stallmart.order.OrderService;
import com.stallmart.order.dto.OrderDTO;
import com.stallmart.order.internal.model.OrderStatus;
import com.stallmart.product.dto.ProductDTO;
import com.stallmart.store.StoreService;
import com.stallmart.store.dto.StoreDTO;
import com.stallmart.store.dto.StoreDecorationDTO;
import com.stallmart.store.internal.model.ProductStatus;
import com.stallmart.store.internal.model.StoreStatus;
import com.stallmart.store.internal.model.StoreStyleStatus;
import com.stallmart.style.dto.StyleDTO;
import com.stallmart.user.UserService;
import com.stallmart.user.dto.UserProfileDTO;
import com.stallmart.user.internal.model.UserRole;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class VendorWorkspaceServiceImplTest {

    @Mock
    private StoreService storeService;

    @Mock
    private OrderService orderService;

    @Mock
    private CartService cartService;

    @Mock
    private UserService userService;

    @Test
    void shouldBuildVendorWorkspace_whenStoreHasOrdersCartsAndUsers() {
        StoreDTO store = store();
        StoreDecorationDTO decoration = decoration(store);
        ProductDTO product = product(store.id());
        OrderDTO paidOrder = order(101L, 10L, store.id(), "COMPLETED", "12.00");
        OrderDTO rejectedOrder = order(102L, 11L, store.id(), "REJECTED", "99.00");
        CartDTO cart = cart(201L, 20L, store.id());
        UserProfileDTO orderUser = user(10L, "下单用户", "CUSTOMER");
        UserProfileDTO cartUser = user(20L, "购物车用户", "CUSTOMER");
        UserProfileDTO unrelatedUser = user(30L, "无关用户", "CUSTOMER");
        UserProfileDTO adminUser = user(40L, "平台管理员", "ADMIN");
        StyleDTO activeStyle = style(6L, "ACTIVE");

        when(storeService.getDecoration(store.id())).thenReturn(decoration);
        when(storeService.listProducts(store.id())).thenReturn(List.of(product));
        when(orderService.listByStore(store.id())).thenReturn(List.of(paidOrder, rejectedOrder));
        when(cartService.listByStore(store.id())).thenReturn(List.of(cart));
        when(userService.listUsers()).thenReturn(List.of(orderUser, cartUser, unrelatedUser, adminUser));
        when(storeService.listActiveStyles()).thenReturn(List.of(activeStyle));

        VendorWorkspaceServiceImpl service = new VendorWorkspaceServiceImpl(
                storeService,
                orderService,
                cartService,
                userService
        );

        VendorWorkspaceDTO workspace = service.buildVendorWorkspace(store);

        assertThat(workspace.store()).isEqualTo(store);
        assertThat(workspace.decoration()).isEqualTo(decoration);
        assertThat(workspace.products()).containsExactly(product);
        assertThat(workspace.orders()).containsExactly(paidOrder, rejectedOrder);
        assertThat(workspace.carts()).containsExactly(cart);
        assertThat(workspace.users()).containsExactly(orderUser, cartUser);
        assertThat(workspace.styles()).containsExactly(activeStyle);
        assertThat(workspace.orderCount()).isEqualTo(2);
        assertThat(workspace.cartCount()).isEqualTo(1);
        assertThat(workspace.salesAmount()).isEqualByComparingTo("12.00");
        verify(storeService).listActiveStyles();
    }

    private StoreDTO store() {
        return new StoreDTO(
                1L,
                2L,
                6L,
                "forestFruitTeaCrayon",
                "小新の水果茶屋",
                "饮品",
                "自然水果",
                "/avatar.png",
                "/cover.png",
                "stall-001",
                "上海环球港店",
                StoreStatus.OPEN
        );
    }

    private StoreDecorationDTO decoration(StoreDTO store) {
        return new StoreDecorationDTO(
                store.id(),
                store.name(),
                store.avatarUrl(),
                store.coverUrl(),
                List.of("/banner.png"),
                store.styleId(),
                store.styleCode(),
                null,
                "customer-storefront-v1",
                null,
                Map.of("primary", "#6F9646"),
                Map.of(),
                Map.of(),
                Map.of(),
                Map.of(),
                List.of(),
                List.of(),
                Map.of(),
                Map.of()
        );
    }

    private ProductDTO product(long storeId) {
        return new ProductDTO(
                1L,
                storeId,
                1L,
                "清爽柠檬",
                "柚子柠檬茶",
                "西柚果肉",
                new BigDecimal("12.00"),
                "/product.png",
                "/product.png",
                "清爽柠檬",
                ProductStatus.ACTIVE,
                1,
                List.of(1L, 2L),
                List.of()
        );
    }

    private OrderDTO order(long id, long userId, long storeId, String status, String totalAmount) {
        return new OrderDTO(
                id,
                "SM20260101000001",
                userId,
                storeId,
                OrderStatus.valueOf(status),
                "1001",
                new BigDecimal(totalAmount),
                null,
                Instant.parse("2026-01-01T00:00:00Z"),
                List.of()
        );
    }

    private CartDTO cart(long id, long userId, long storeId) {
        return new CartDTO(id, userId, storeId, CartStatus.ACTIVE, Instant.parse("2026-01-01T00:00:00Z"), List.of());
    }

    private UserProfileDTO user(long id, String nickname, String role) {
        return new UserProfileDTO(id, nickname, "/avatar.png", null, false, UserRole.valueOf(role));
    }

    private StyleDTO style(long id, String status) {
        return new StyleDTO(id, "森系水果茶", "forestFruitTeaCrayon", "/preview.png", null, StoreStyleStatus.valueOf(status), 1);
    }
}
