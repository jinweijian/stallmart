package com.stallmart.order.internal.service;

import com.stallmart.support.exception.ErrorCode;
import com.stallmart.support.exception.AppException;
import com.stallmart.order.dto.CreateOrderParams;
import com.stallmart.order.dto.OrderCountsDTO;
import com.stallmart.order.dto.OrderItemDTO;
import com.stallmart.order.dto.OrderDTO;
import com.stallmart.product.dto.ProductDTO;
import com.stallmart.store.StoreService;
import com.stallmart.order.OrderService;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Service;

@Service
public class OrderServiceImpl implements OrderService {

    private final StoreService catalogService;
    private final AtomicLong idSequence = new AtomicLong(1);
    private final Map<Long, OrderDTO> orders = new ConcurrentHashMap<>();

    public OrderServiceImpl(StoreService catalogService) {
        this.catalogService = catalogService;
        create(1L, new CreateOrderParams(
                1L,
                "少冰，现场取餐",
                List.of(new CreateOrderParams.Item(1L, 1, "中杯"))
        ));
    }

    @Override
    public OrderDTO create(long userId, CreateOrderParams request) {
        catalogService.getStore(request.storeId());
        List<OrderItemDTO> items = request.items().stream()
                .map(item -> toOrderItem(request.storeId(), item))
                .toList();
        BigDecimal total = items.stream()
                .map(item -> item.unitPrice().multiply(BigDecimal.valueOf(item.quantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        long id = idSequence.getAndIncrement();
        OrderDTO order = new OrderDTO(
                id,
                buildOrderNo(id),
                userId,
                request.storeId(),
                "NEW",
                String.format("%04d", 1000 + id),
                total,
                request.remark(),
                Instant.now(),
                items
        );
        orders.put(id, order);
        return order;
    }

    @Override
    public OrderDTO get(long userId, long orderId) {
        OrderDTO order = find(orderId);
        if (!order.userId().equals(userId)) {
            throw new AppException(ErrorCode.NOT_FOUND);
        }
        return order;
    }

    @Override
    public List<OrderDTO> list(long userId) {
        return orders.values().stream()
                .filter(order -> order.userId().equals(userId))
                .sorted((left, right) -> right.createdAt().compareTo(left.createdAt()))
                .toList();
    }

    @Override
    public List<OrderDTO> listAll() {
        return orders.values().stream()
                .sorted((left, right) -> right.createdAt().compareTo(left.createdAt()))
                .toList();
    }

    @Override
    public List<OrderDTO> listByStore(long storeId) {
        catalogService.getStore(storeId);
        return orders.values().stream()
                .filter(order -> order.storeId().equals(storeId))
                .sorted((left, right) -> right.createdAt().compareTo(left.createdAt()))
                .toList();
    }

    @Override
    public OrderCountsDTO counts(long userId) {
        List<OrderDTO> userOrders = list(userId);
        long pending = userOrders.stream().filter(order -> order.status().equals("NEW") || order.status().equals("ACCEPTED")).count();
        long preparing = userOrders.stream().filter(order -> order.status().equals("PREPARING") || order.status().equals("READY")).count();
        long completed = userOrders.stream().filter(order -> order.status().equals("COMPLETED")).count();
        return new OrderCountsDTO(userOrders.size(), pending, preparing, completed);
    }

    @Override
    public OrderDTO accept(long id) {
        return transition(id, "ACCEPTED", "NEW");
    }

    @Override
    public OrderDTO reject(long id) {
        return transition(id, "REJECTED", "NEW");
    }

    @Override
    public OrderDTO prepare(long id) {
        return transition(id, "PREPARING", "ACCEPTED");
    }

    @Override
    public OrderDTO ready(long id) {
        return transition(id, "READY", "PREPARING");
    }

    @Override
    public OrderDTO complete(long id) {
        return transition(id, "COMPLETED", "READY");
    }

    private OrderItemDTO toOrderItem(Long storeId, CreateOrderParams.Item item) {
        ProductDTO product = catalogService.getProduct(item.productId());
        if (!product.storeId().equals(storeId)) {
            throw new AppException(ErrorCode.BAD_REQUEST);
        }
        return new OrderItemDTO(product.id(), product.name(), item.quantity(), product.price(), item.specsText());
    }

    private OrderDTO transition(long id, String nextStatus, String requiredCurrentStatus) {
        OrderDTO current = find(id);
        if (!current.status().equals(requiredCurrentStatus)) {
            throw new AppException(ErrorCode.INVALID_ORDER_STATUS);
        }
        OrderDTO updated = new OrderDTO(
                current.id(),
                current.orderNo(),
                current.userId(),
                current.storeId(),
                nextStatus,
                current.confirmCode(),
                current.totalAmount(),
                current.remark(),
                current.createdAt(),
                current.items()
        );
        orders.put(id, updated);
        return updated;
    }

    private OrderDTO find(long id) {
        OrderDTO order = orders.get(id);
        if (order == null) {
            throw new AppException(ErrorCode.NOT_FOUND);
        }
        return order;
    }

    private String buildOrderNo(long id) {
        String date = DateTimeFormatter.BASIC_ISO_DATE.format(java.time.LocalDate.now());
        return "SM" + date + String.format("%06d", id);
    }
}
