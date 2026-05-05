package com.stallmart.order.internal.repository;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "orders")
public class OrderEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    @Column(name = "order_no")
    public String orderNo;
    @Column(name = "user_id")
    public Long userId;
    @Column(name = "store_id")
    public Long storeId;
    public String status;
    @Column(name = "confirm_code")
    public String confirmCode;
    @Column(name = "total_amount")
    public BigDecimal totalAmount;
    public String remark;
    @Column(name = "created_at")
    public Instant createdAt;
}
