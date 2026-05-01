package com.stallmart.order;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.stallmart.support.exception.AppException;
import com.stallmart.order.dto.CreateOrderParams;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@DisplayName("订单服务测试")
public class OrderServiceTest {

    @Autowired
    private OrderService orderService;

    @Test
    void shouldCalculateTotalAmount_whenCreateOrder() {
        CreateOrderParams request = new CreateOrderParams(
                1L,
                "少冰",
                List.of(new CreateOrderParams.Item(1L, 2, "大杯"))
        );

        var order = orderService.create(1L, request);

        assertThat(order.status()).isEqualTo("NEW");
        assertThat(order.totalAmount()).isEqualByComparingTo(new BigDecimal("24.00"));
        assertThat(order.items()).hasSize(1);
        assertThat(order.items().getFirst().productName()).isEqualTo("百香果柠檬茶");
    }

    @Test
    void shouldFollowVendorWorkflow_whenStatusChanges() {
        var order = orderService.create(1L, new CreateOrderParams(
                1L,
                null,
                List.of(new CreateOrderParams.Item(2L, 1, null))
        ));

        assertThat(orderService.accept(order.id()).status()).isEqualTo("ACCEPTED");
        assertThat(orderService.prepare(order.id()).status()).isEqualTo("PREPARING");
        assertThat(orderService.ready(order.id()).status()).isEqualTo("READY");
        assertThat(orderService.complete(order.id()).status()).isEqualTo("COMPLETED");
    }

    @Test
    void shouldThrowException_whenStatusTransitionInvalid() {
        var order = orderService.create(1L, new CreateOrderParams(
                1L,
                null,
                List.of(new CreateOrderParams.Item(3L, 1, null))
        ));

        assertThatThrownBy(() -> orderService.ready(order.id()))
                .isInstanceOf(AppException.class)
                .extracting("errorCode")
                .isNotNull();
    }
}
