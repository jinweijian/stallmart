package com.stallmart.order.internal.service;

import com.stallmart.order.internal.model.OrderStatus;
import com.stallmart.support.exception.AppException;
import com.stallmart.support.exception.ErrorCode;
import org.springframework.stereotype.Component;

@Component
public class OrderStatusTransition {

    public OrderStatus next(OrderStatus current, String action) {
        return switch (action) {
            case "accept" -> require(current, OrderStatus.NEW, OrderStatus.ACCEPTED);
            case "reject" -> require(current, OrderStatus.NEW, OrderStatus.REJECTED);
            case "prepare" -> require(current, OrderStatus.ACCEPTED, OrderStatus.PREPARING);
            case "ready" -> require(current, OrderStatus.PREPARING, OrderStatus.READY);
            case "complete" -> require(current, OrderStatus.READY, OrderStatus.COMPLETED);
            default -> throw new AppException(ErrorCode.BAD_REQUEST);
        };
    }

    private OrderStatus require(OrderStatus current, OrderStatus required, OrderStatus next) {
        if (current != required) {
            throw new AppException(ErrorCode.INVALID_ORDER_STATUS);
        }
        return next;
    }
}
