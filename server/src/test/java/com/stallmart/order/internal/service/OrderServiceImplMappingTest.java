package com.stallmart.order.internal.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.stallmart.order.dto.OrderDTO;
import com.stallmart.order.internal.model.OrderStatus;
import com.stallmart.order.internal.repository.OrderEntity;
import com.stallmart.order.internal.repository.OrderItemEntity;
import com.stallmart.order.internal.repository.OrderItemRepository;
import com.stallmart.order.internal.repository.OrderRepository;
import com.stallmart.store.StoreService;
import com.stallmart.store.dto.StoreDTO;
import com.stallmart.store.internal.model.StoreStatus;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplMappingTest {

    @Mock
    private StoreService storeService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemRepository itemRepository;

    @Test
    void shouldLoadOrderItemsInBatch_whenListByStore() {
        OrderEntity first = order(1L, 10L);
        OrderEntity second = order(2L, 20L);
        when(storeService.getStore(1L)).thenReturn(store());
        when(orderRepository.findByStoreIdOrderByCreatedAtDesc(1L)).thenReturn(List.of(first, second));
        when(itemRepository.findByOrderIdInOrderByOrderIdAscIdAsc(List.of(1L, 2L)))
                .thenReturn(List.of(item(100L, 1L), item(200L, 2L)));
        OrderServiceImpl service = new OrderServiceImpl(
                storeService,
                orderRepository,
                itemRepository,
                new OrderStatusTransition(),
                new OrderNumberGenerator()
        );

        List<OrderDTO> orders = service.listByStore(1L);

        assertThat(orders).hasSize(2);
        assertThat(orders.get(0).items()).hasSize(1);
        assertThat(orders.get(1).items()).hasSize(1);
        verify(itemRepository).findByOrderIdInOrderByOrderIdAscIdAsc(List.of(1L, 2L));
        verify(itemRepository, never()).findByOrderIdOrderByIdAsc(1L);
        verify(itemRepository, never()).findByOrderIdOrderByIdAsc(2L);
    }

    private StoreDTO store() {
        return new StoreDTO(1L, 2L, 6L, "forestFruitTeaCrayon", "小新の水果茶屋", "饮品", "自然水果",
                "/avatar.png", "/cover.png", "stall-001", "上海环球港店", StoreStatus.OPEN);
    }

    private OrderEntity order(long id, long userId) {
        OrderEntity order = new OrderEntity();
        order.id = id;
        order.orderNo = "SM2026051000000" + id;
        order.userId = userId;
        order.storeId = 1L;
        order.status = OrderStatus.NEW.name();
        order.confirmCode = "100" + id;
        order.totalAmount = new BigDecimal("12.00");
        order.createdAt = Instant.parse("2026-05-10T00:00:00Z");
        return order;
    }

    private OrderItemEntity item(long id, long orderId) {
        OrderItemEntity item = new OrderItemEntity();
        item.id = id;
        item.orderId = orderId;
        item.productId = 1L;
        item.productName = "柚子柠檬茶";
        item.quantity = 1;
        item.unitPrice = new BigDecimal("12.00");
        item.specsText = "中杯";
        return item;
    }
}
