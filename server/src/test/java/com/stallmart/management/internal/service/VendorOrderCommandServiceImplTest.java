package com.stallmart.management.internal.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.stallmart.management.internal.security.AdminAccessGuard;
import com.stallmart.order.OrderService;
import com.stallmart.order.dto.OrderDTO;
import com.stallmart.store.dto.StoreDTO;
import com.stallmart.support.exception.AppException;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class VendorOrderCommandServiceImplTest {

    @Mock
    private OrderService orderService;

    @Mock
    private AdminAccessGuard accessGuard;

    @Test
    void shouldCheckStoreOwnershipAndAcceptOrder_whenActionIsAccept() {
        StoreDTO store = store();
        OrderDTO order = order("NEW");
        OrderDTO accepted = order("ACCEPTED");
        when(orderService.getAdmin(order.id())).thenReturn(order);
        when(orderService.accept(order.id())).thenReturn(accepted);

        VendorOrderCommandServiceImpl service = new VendorOrderCommandServiceImpl(orderService, accessGuard);

        OrderDTO result = service.transition(store, order.id(), "accept");

        assertThat(result).isEqualTo(accepted);
        verify(accessGuard).requireOrderInStore(store, order);
    }

    @Test
    void shouldRejectUnknownAction_whenActionIsNotSupported() {
        StoreDTO store = store();
        OrderDTO order = order("NEW");
        when(orderService.getAdmin(order.id())).thenReturn(order);

        VendorOrderCommandServiceImpl service = new VendorOrderCommandServiceImpl(orderService, accessGuard);

        assertThatThrownBy(() -> service.transition(store, order.id(), "archive"))
                .isInstanceOf(AppException.class);
        verify(accessGuard).requireOrderInStore(store, order);
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
                "OPEN"
        );
    }

    private OrderDTO order(String status) {
        return new OrderDTO(
                101L,
                "SM20260101000001",
                10L,
                1L,
                status,
                "1001",
                new BigDecimal("12.00"),
                null,
                Instant.parse("2026-01-01T00:00:00Z"),
                List.of()
        );
    }
}
