package com.stallmart.management.internal.service;

import com.stallmart.management.VendorOrderCommandService;
import com.stallmart.management.internal.security.AdminAccessGuard;
import com.stallmart.order.OrderService;
import com.stallmart.order.dto.OrderDTO;
import com.stallmart.store.dto.StoreDTO;
import com.stallmart.support.exception.AppException;
import com.stallmart.support.exception.ErrorCode;
import org.springframework.stereotype.Service;

@Service
public class VendorOrderCommandServiceImpl implements VendorOrderCommandService {

    private final OrderService orderService;
    private final AdminAccessGuard accessGuard;

    public VendorOrderCommandServiceImpl(OrderService orderService, AdminAccessGuard accessGuard) {
        this.orderService = orderService;
        this.accessGuard = accessGuard;
    }

    @Override
    public OrderDTO transition(StoreDTO store, long orderId, String action) {
        OrderDTO order = orderService.getAdmin(orderId);
        accessGuard.requireOrderInStore(store, order);
        return switch (action) {
            case "accept" -> orderService.accept(orderId);
            case "reject" -> orderService.reject(orderId);
            case "prepare" -> orderService.prepare(orderId);
            case "ready" -> orderService.ready(orderId);
            case "complete" -> orderService.complete(orderId);
            default -> throw new AppException(ErrorCode.BAD_REQUEST);
        };
    }
}
