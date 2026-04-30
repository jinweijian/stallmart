package com.stallmart.controller;

import com.stallmart.common.result.Result;
import com.stallmart.model.dto.CreateOrderDTO;
import com.stallmart.model.entity.Order;
import com.stallmart.model.vo.OrderItemVO;
import com.stallmart.model.vo.OrderVO;
import com.stallmart.repository.OrderItemMapper;
import com.stallmart.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "订单", description = "订单创建、查询、状态更新")
@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final OrderItemMapper orderItemMapper;

    @Operation(summary = "创建订单")
    @PostMapping
    public Result<OrderVO> createOrder(@Valid @RequestBody CreateOrderDTO dto, HttpServletRequest request) {
        Long customerId = (Long) request.getAttribute("userId");
        Order order = orderService.create(dto, customerId);
        return Result.success(toVO(order));
    }

    @Operation(summary = "获取订单详情")
    @GetMapping("/{id}")
    public Result<OrderVO> getOrder(@PathVariable Long id) {
        Order order = orderService.findById(id);
        return Result.success(toVO(order));
    }

    @Operation(summary = "获取我的订单列表")
    @GetMapping
    public Result<List<OrderVO>> getMyOrders(HttpServletRequest request) {
        Long customerId = (Long) request.getAttribute("userId");
        List<Order> orders = orderService.findByCustomerId(customerId);
        List<OrderVO> voList = orders.stream().map(this::toVO).collect(Collectors.toList());
        return Result.success(voList);
    }

    @Operation(summary = "摊主-接单")
    @PutMapping("/{id}/accept")
    public Result<Void> accept(@PathVariable Long id) {
        orderService.accept(id);
        return Result.success();
    }

    @Operation(summary = "摊主-拒单")
    @PutMapping("/{id}/reject")
    public Result<Void> reject(@PathVariable Long id, @RequestBody String reason) {
        orderService.reject(id, reason);
        return Result.success();
    }

    @Operation(summary = "摊主-备餐中")
    @PutMapping("/{id}/prepare")
    public Result<Void> prepare(@PathVariable Long id) {
        orderService.prepare(id);
        return Result.success();
    }

    @Operation(summary = "摊主-待取餐")
    @PutMapping("/{id}/ready")
    public Result<Void> ready(@PathVariable Long id) {
        orderService.ready(id);
        return Result.success();
    }

    @Operation(summary = "完成订单")
    @PutMapping("/{id}/complete")
    public Result<Void> complete(@PathVariable Long id) {
        orderService.complete(id);
        return Result.success();
    }

    private OrderVO toVO(Order order) {
        List<OrderItemVO> items = orderItemMapper.selectList(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<com.stallmart.model.entity.OrderItem>()
                .eq(com.stallmart.model.entity.OrderItem::getOrderId, order.getId())
        ).stream().map(item -> OrderItemVO.builder()
                .id(item.getId())
                .productId(item.getProductId())
                .productName(item.getProductName())
                .price(item.getPrice())
                .quantity(item.getQuantity())
                .subtotal(item.getSubtotal())
                .specName(item.getSpecName())
                .build())
        .collect(Collectors.toList());

        return OrderVO.builder()
                .id(order.getId())
                .orderNo(order.getOrderNo())
                .customerId(order.getCustomerId())
                .storeId(order.getStoreId())
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus())
                .confirmCode(order.getConfirmCode())
                .remark(order.getRemark())
                .createdAt(order.getCreatedAt())
                .items(items)
                .build();
    }
}
