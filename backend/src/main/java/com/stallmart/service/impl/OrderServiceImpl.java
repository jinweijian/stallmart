package com.stallmart.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.stallmart.common.exception.BusinessException;
import com.stallmart.common.result.ErrorCode;
import com.stallmart.model.dto.CreateOrderDTO;
import com.stallmart.model.entity.*;
import com.stallmart.repository.OrderItemMapper;
import com.stallmart.repository.OrderMapper;
import com.stallmart.repository.ProductMapper;
import com.stallmart.service.OrderService;
import com.stallmart.service.StoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final ProductMapper productMapper;
    private final StoreService storeService;

    @Override
    @Transactional
    public Order create(CreateOrderDTO dto, Long customerId) {
        // 验证店铺存在
        storeService.findById(dto.getStoreId());

        // 生成订单号
        String orderNo = generateOrderNo();
        // 生成确认码（手机尾号后4位）
        String confirmCode = String.format("%04d", (int)(Math.random() * 10000));

        // 计算总金额
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (CreateOrderDTO.OrderItemDTO itemDTO : dto.getItems()) {
            Product product = productMapper.selectById(itemDTO.getProductId());
            if (product != null && !product.getDeleted()) {
                totalAmount = totalAmount.add(product.getPrice().multiply(new BigDecimal(itemDTO.getQuantity())));
            }
        }

        // 创建订单
        Order order = new Order();
        order.setOrderNo(orderNo);
        order.setCustomerId(customerId);
        order.setStoreId(dto.getStoreId());
        order.setTotalAmount(totalAmount);
        order.setStatus("pending");
        order.setConfirmCode(confirmCode);
        order.setRemark(dto.getRemark());
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        order.setDeleted(false);
        orderMapper.insert(order);

        // 创建订单明细
        for (CreateOrderDTO.OrderItemDTO itemDTO : dto.getItems()) {
            Product product = productMapper.selectById(itemDTO.getProductId());
            if (product != null && !product.getDeleted()) {
                OrderItem item = new OrderItem();
                item.setOrderId(order.getId());
                item.setProductId(product.getId());
                item.setProductName(product.getName());
                item.setPrice(product.getPrice());
                item.setQuantity(itemDTO.getQuantity());
                item.setSubtotal(product.getPrice().multiply(new BigDecimal(itemDTO.getQuantity())));
                item.setSpecName(itemDTO.getSpecName());
                item.setCreatedAt(LocalDateTime.now());
                item.setUpdatedAt(LocalDateTime.now());
                item.setDeleted(false);
                orderItemMapper.insert(item);
            }
        }

        return order;
    }

    @Override
    public Order findById(Long id) {
        Order order = orderMapper.selectById(id);
        if (order == null || order.getDeleted()) {
            throw new BusinessException(ErrorCode.ORDER_NOT_FOUND);
        }
        return order;
    }

    @Override
    public List<Order> findByCustomerId(Long customerId) {
        return orderMapper.selectList(
            new LambdaQueryWrapper<Order>()
                .eq(Order::getCustomerId, customerId)
                .eq(Order::getDeleted, false)
                .orderByDesc(Order::getCreatedAt)
        );
    }

    @Override
    public List<Order> findByStoreId(Long storeId) {
        return orderMapper.selectList(
            new LambdaQueryWrapper<Order>()
                .eq(Order::getStoreId, storeId)
                .eq(Order::getDeleted, false)
                .orderByDesc(Order::getCreatedAt)
        );
    }

    @Override
    public void updateStatus(Long id, String status) {
        Order order = findById(id);
        order.setStatus(status);
        order.setUpdatedAt(LocalDateTime.now());
        orderMapper.updateById(order);
    }

    @Override
    public void accept(Long id) {
        Order order = findById(id);
        if (!"pending".equals(order.getStatus())) {
            throw new BusinessException(ErrorCode.ORDER_STATUS_ERROR);
        }
        updateStatus(id, "accepted");
    }

    @Override
    public void reject(Long id, String reason) {
        Order order = findById(id);
        if (!"pending".equals(order.getStatus())) {
            throw new BusinessException(ErrorCode.ORDER_STATUS_ERROR);
        }
        order.setRemark((order.getRemark() != null ? order.getRemark() : "") + " [拒单原因:" + reason + "]");
        updateStatus(id, "rejected");
    }

    @Override
    public void prepare(Long id) {
        Order order = findById(id);
        if (!"accepted".equals(order.getStatus())) {
            throw new BusinessException(ErrorCode.ORDER_STATUS_ERROR);
        }
        updateStatus(id, "preparing");
    }

    @Override
    public void ready(Long id) {
        Order order = findById(id);
        if (!"preparing".equals(order.getStatus())) {
            throw new BusinessException(ErrorCode.ORDER_STATUS_ERROR);
        }
        updateStatus(id, "ready");
    }

    @Override
    public void complete(Long id) {
        Order order = findById(id);
        if (!"ready".equals(order.getStatus())) {
            throw new BusinessException(ErrorCode.ORDER_STATUS_ERROR);
        }
        updateStatus(id, "completed");
    }

    private String generateOrderNo() {
        return "ORD" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
               + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
