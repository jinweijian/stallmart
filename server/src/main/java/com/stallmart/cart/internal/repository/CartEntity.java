package com.stallmart.cart.internal.repository;

import com.stallmart.cart.internal.model.CartStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "cart")
public class CartEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    @Column(name = "user_id")
    public Long userId;
    @Column(name = "store_id")
    public Long storeId;
    @Enumerated(EnumType.STRING)
    public CartStatus status;
    @Column(name = "updated_at")
    public Instant updatedAt;
}
