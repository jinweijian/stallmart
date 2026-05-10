package com.stallmart.order.internal.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.stallmart.order.internal.model.OrderStatus;
import com.stallmart.support.exception.AppException;
import com.stallmart.support.exception.ErrorCode;
import org.junit.jupiter.api.Test;

class OrderStatusTransitionTest {

    @Test
    void shouldReturnNextStatus_whenActionIsAllowed() {
        OrderStatusTransition transition = new OrderStatusTransition();

        assertThat(transition.next(OrderStatus.NEW, "accept")).isEqualTo(OrderStatus.ACCEPTED);
        assertThat(transition.next(OrderStatus.ACCEPTED, "prepare")).isEqualTo(OrderStatus.PREPARING);
        assertThat(transition.next(OrderStatus.PREPARING, "ready")).isEqualTo(OrderStatus.READY);
        assertThat(transition.next(OrderStatus.READY, "complete")).isEqualTo(OrderStatus.COMPLETED);
        assertThat(transition.next(OrderStatus.NEW, "reject")).isEqualTo(OrderStatus.REJECTED);
    }

    @Test
    void shouldThrowInvalidOrderStatus_whenActionIsNotAllowedFromCurrentStatus() {
        OrderStatusTransition transition = new OrderStatusTransition();

        assertThatThrownBy(() -> transition.next(OrderStatus.NEW, "ready"))
                .isInstanceOf(AppException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.INVALID_ORDER_STATUS);
    }
}
