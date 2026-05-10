package com.stallmart.order.internal.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import org.springframework.stereotype.Component;

@Component
public class OrderNumberGenerator {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.BASIC_ISO_DATE;

    public String pendingOrderNo() {
        return "SM" + DATE_FORMAT.format(LocalDate.now())
                + String.format("%09d", Math.floorMod(System.nanoTime(), 1_000_000_000L));
    }

    public String orderNo(long id) {
        return orderNo(LocalDate.now(), id);
    }

    String orderNo(LocalDate date, long id) {
        return "SM" + DATE_FORMAT.format(date) + String.format("%06d", id);
    }

    public String confirmCode(long id) {
        return String.format("%04d", 1000 + id);
    }
}
