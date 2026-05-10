package com.stallmart.management.internal.service;

import com.stallmart.cart.CartService;
import com.stallmart.cart.dto.CartDTO;
import com.stallmart.management.VendorWorkspaceService;
import com.stallmart.management.dto.VendorWorkspaceDTO;
import com.stallmart.order.OrderService;
import com.stallmart.order.dto.OrderDTO;
import com.stallmart.store.StoreService;
import com.stallmart.store.dto.StoreDTO;
import com.stallmart.user.UserService;
import com.stallmart.user.dto.UserProfileDTO;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class VendorWorkspaceServiceImpl implements VendorWorkspaceService {

    private final StoreService storeService;
    private final OrderService orderService;
    private final CartService cartService;
    private final UserService userService;

    public VendorWorkspaceServiceImpl(
            StoreService storeService,
            OrderService orderService,
            CartService cartService,
            UserService userService
    ) {
        this.storeService = storeService;
        this.orderService = orderService;
        this.cartService = cartService;
        this.userService = userService;
    }

    @Override
    public VendorWorkspaceDTO buildVendorWorkspace(StoreDTO store) {
        List<OrderDTO> orders = orderService.listByStore(store.id());
        List<CartDTO> carts = cartService.listByStore(store.id());
        return new VendorWorkspaceDTO(
                store,
                storeService.getDecoration(store.id()),
                storeService.listProducts(store.id()),
                orders,
                carts,
                listUsersForStore(store.id()),
                storeService.listActiveStyles(),
                orders.size(),
                carts.size(),
                salesAmount(orders)
        );
    }

    @Override
    public List<UserProfileDTO> listUsersForStore(long storeId) {
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
