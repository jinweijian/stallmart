package com.stallmart.order.dto;

public record OrderCountsDTO(long total, long pending, long preparing, long completed) {
}
