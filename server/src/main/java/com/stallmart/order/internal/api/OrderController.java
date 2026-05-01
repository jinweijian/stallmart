package com.stallmart.order.internal.api;

import com.stallmart.support.web.Result;
import com.stallmart.support.security.CurrentUserResolver;
import com.stallmart.order.dto.CreateOrderParams;
import com.stallmart.order.dto.OrderCountsDTO;
import com.stallmart.order.dto.OrderDTO;
import com.stallmart.order.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;
    private final CurrentUserResolver currentUserResolver;

    public OrderController(OrderService orderService, CurrentUserResolver currentUserResolver) {
        this.orderService = orderService;
        this.currentUserResolver = currentUserResolver;
    }

    @PostMapping
    public Result<OrderDTO> create(@Valid @RequestBody CreateOrderParams request, HttpServletRequest servletRequest) {
        long userId = currentUserResolver.resolve(servletRequest);
        return Result.success(orderService.create(userId, request));
    }

    @GetMapping("/{id}")
    public Result<OrderDTO> get(@PathVariable long id, HttpServletRequest servletRequest) {
        long userId = currentUserResolver.resolve(servletRequest);
        return Result.success(orderService.get(userId, id));
    }

    @GetMapping
    public Result<List<OrderDTO>> list(HttpServletRequest servletRequest) {
        long userId = currentUserResolver.resolve(servletRequest);
        return Result.success(orderService.list(userId));
    }

    @GetMapping("/counts")
    public Result<OrderCountsDTO> counts(HttpServletRequest servletRequest) {
        long userId = currentUserResolver.resolve(servletRequest);
        return Result.success(orderService.counts(userId));
    }

    @PutMapping("/{id}/accept")
    public Result<OrderDTO> accept(@PathVariable long id) {
        return Result.success(orderService.accept(id));
    }

    @PutMapping("/{id}/reject")
    public Result<OrderDTO> reject(@PathVariable long id) {
        return Result.success(orderService.reject(id));
    }

    @PutMapping("/{id}/prepare")
    public Result<OrderDTO> prepare(@PathVariable long id) {
        return Result.success(orderService.prepare(id));
    }

    @PutMapping("/{id}/ready")
    public Result<OrderDTO> ready(@PathVariable long id) {
        return Result.success(orderService.ready(id));
    }

    @PutMapping("/{id}/complete")
    public Result<OrderDTO> complete(@PathVariable long id) {
        return Result.success(orderService.complete(id));
    }
}
