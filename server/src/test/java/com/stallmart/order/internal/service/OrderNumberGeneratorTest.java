package com.stallmart.order.internal.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class OrderNumberGeneratorTest {

    @Test
    void shouldBuildStableOrderNoWithDateAndPaddedId() {
        OrderNumberGenerator generator = new OrderNumberGenerator();

        String orderNo = generator.orderNo(LocalDate.of(2026, 5, 10), 42L);

        assertThat(orderNo).isEqualTo("SM20260510000042");
    }

    @Test
    void shouldBuildFourDigitConfirmCodeFromOrderId() {
        OrderNumberGenerator generator = new OrderNumberGenerator();

        String confirmCode = generator.confirmCode(42L);

        assertThat(confirmCode).isEqualTo("1042");
    }
}
