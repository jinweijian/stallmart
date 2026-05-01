package com.stallmart.management.internal.security;

import com.stallmart.order.dto.OrderDTO;
import com.stallmart.product.dto.ProductDTO;
import com.stallmart.store.StoreService;
import com.stallmart.store.dto.StoreDTO;
import com.stallmart.support.exception.AppException;
import com.stallmart.support.exception.ErrorCode;
import com.stallmart.support.security.CurrentUserResolver;
import com.stallmart.user.UserService;
import com.stallmart.user.dto.UserProfileDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

@Component
public class AdminAccessGuard {

    private final CurrentUserResolver currentUserResolver;
    private final UserService userService;
    private final StoreService storeService;

    public AdminAccessGuard(CurrentUserResolver currentUserResolver, UserService userService, StoreService storeService) {
        this.currentUserResolver = currentUserResolver;
        this.userService = userService;
        this.storeService = storeService;
    }

    public UserProfileDTO requirePlatformAdmin(HttpServletRequest request) {
        UserProfileDTO user = currentUser(request);
        if (!user.role().equals("ADMIN")) {
            throw new AppException(ErrorCode.FORBIDDEN);
        }
        return user;
    }

    public StoreDTO requireVendorStore(HttpServletRequest request) {
        UserProfileDTO user = currentUser(request);
        if (user.role().equals("ADMIN")) {
            String storeId = request.getHeader("X-Store-Id");
            if (storeId == null || storeId.isBlank()) {
                throw new AppException(ErrorCode.FORBIDDEN);
            }
            try {
                return storeService.getStore(Long.parseLong(storeId));
            } catch (NumberFormatException exception) {
                throw new AppException(ErrorCode.FORBIDDEN);
            }
        }
        if (!user.role().equals("VENDOR")) {
            throw new AppException(ErrorCode.FORBIDDEN);
        }
        return storeService.listStoresByOwner(user.id()).stream()
                .findFirst()
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));
    }

    public void requireProductInStore(StoreDTO store, ProductDTO product) {
        if (!product.storeId().equals(store.id())) {
            throw new AppException(ErrorCode.FORBIDDEN);
        }
    }

    public void requireOrderInStore(StoreDTO store, OrderDTO order) {
        if (!order.storeId().equals(store.id())) {
            throw new AppException(ErrorCode.FORBIDDEN);
        }
    }

    private UserProfileDTO currentUser(HttpServletRequest request) {
        return userService.getProfile(currentUserResolver.resolve(request));
    }
}
