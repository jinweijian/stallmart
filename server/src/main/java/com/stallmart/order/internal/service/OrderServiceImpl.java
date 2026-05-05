package com.stallmart.order.internal.service;

import com.stallmart.order.OrderService;
import com.stallmart.order.dto.CreateOrderParams;
import com.stallmart.order.dto.OrderCountsDTO;
import com.stallmart.order.dto.OrderDTO;
import com.stallmart.order.dto.OrderItemDTO;
import com.stallmart.order.internal.repository.OrderEntity;
import com.stallmart.order.internal.repository.OrderItemEntity;
import com.stallmart.order.internal.repository.OrderItemRepository;
import com.stallmart.order.internal.repository.OrderRepository;
import com.stallmart.product.dto.ProductDTO;
import com.stallmart.store.StoreService;
import com.stallmart.support.exception.AppException;
import com.stallmart.support.exception.ErrorCode;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderServiceImpl implements OrderService {

    private final StoreService catalogService;
    private final OrderRepository orderRepository;
    private final OrderItemRepository itemRepository;

    public OrderServiceImpl(StoreService catalogService, OrderRepository orderRepository, OrderItemRepository itemRepository) {
        this.catalogService = catalogService;
        this.orderRepository = orderRepository;
        this.itemRepository = itemRepository;
    }

    @Override
    @Transactional
    public OrderDTO create(long userId, CreateOrderParams request) {
        catalogService.getStore(request.storeId());
        List<OrderItemDTO> items = request.items().stream()
                .map(item -> toOrderItem(request.storeId(), item))
                .toList();
        BigDecimal total = items.stream()
                .map(item -> item.unitPrice().multiply(BigDecimal.valueOf(item.quantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        OrderEntity order = new OrderEntity();
        order.userId = userId;
        order.storeId = request.storeId();
        order.status = "NEW";
        order.confirmCode = "1000";
        order.totalAmount = total;
        order.remark = request.remark();
        order.createdAt = Instant.now();
        OrderEntity saved = orderRepository.save(order);
        saved.orderNo = buildOrderNo(saved.id);
        saved.confirmCode = String.format("%04d", 1000 + saved.id);
        saved = orderRepository.save(saved);

        for (OrderItemDTO item : items) {
            OrderItemEntity entity = new OrderItemEntity();
            entity.orderId = saved.id;
            entity.productId = item.productId();
            entity.productName = item.productName();
            entity.quantity = item.quantity();
            entity.unitPrice = item.unitPrice();
            entity.specsText = item.specsText();
            itemRepository.save(entity);
        }
        return toDTO(saved);
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
    public OrderDTO getAdmin(long orderId) {
        return find(orderId);
    }

    @Override
    public List<OrderDTO> list(long userId) {
        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId).stream().map(this::toDTO).toList();
    }

    @Override
    public List<OrderDTO> listAll() {
        return orderRepository.findAllByOrderByCreatedAtDesc().stream().map(this::toDTO).toList();
    }

    @Override
    public List<OrderDTO> listByStore(long storeId) {
        catalogService.getStore(storeId);
        return orderRepository.findByStoreIdOrderByCreatedAtDesc(storeId).stream().map(this::toDTO).toList();
    }

    @Override
    public List<OrderDTO> listByStoreAndUser(long storeId, long userId) {
        catalogService.getStore(storeId);
        return orderRepository.findByStoreIdAndUserIdOrderByCreatedAtDesc(storeId, userId).stream()
                .map(this::toDTO)
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
    @Transactional
    public OrderDTO accept(long id) {
        return transition(id, "ACCEPTED", "NEW");
    }

    @Override
    @Transactional
    public OrderDTO reject(long id) {
        return transition(id, "REJECTED", "NEW");
    }

    @Override
    @Transactional
    public OrderDTO prepare(long id) {
        return transition(id, "PREPARING", "ACCEPTED");
    }

    @Override
    @Transactional
    public OrderDTO ready(long id) {
        return transition(id, "READY", "PREPARING");
    }

    @Override
    @Transactional
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
        OrderEntity current = orderRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));
        if (!current.status.equals(requiredCurrentStatus)) {
            throw new AppException(ErrorCode.INVALID_ORDER_STATUS);
        }
        current.status = nextStatus;
        return toDTO(orderRepository.save(current));
    }

    private OrderDTO find(long id) {
        return toDTO(orderRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND)));
    }

    private OrderDTO toDTO(OrderEntity order) {
        return new OrderDTO(
                order.id,
                order.orderNo,
                order.userId,
                order.storeId,
                order.status,
                order.confirmCode,
                order.totalAmount,
                order.remark,
                order.createdAt,
                itemRepository.findByOrderIdOrderByIdAsc(order.id).stream()
                        .map(this::toItemDTO)
                        .toList()
        );
    }

    private OrderItemDTO toItemDTO(OrderItemEntity item) {
        return new OrderItemDTO(item.productId, item.productName, item.quantity, item.unitPrice, item.specsText);
    }

    private String buildOrderNo(long id) {
        String date = DateTimeFormatter.BASIC_ISO_DATE.format(LocalDate.now());
        return "SM" + date + String.format("%06d", id);
    }
}
