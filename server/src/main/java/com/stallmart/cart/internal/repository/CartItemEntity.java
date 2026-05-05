package com.stallmart.cart.internal.repository;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "cart_item")
public class CartItemEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    @Column(name = "cart_id")
    public Long cartId;
    @Column(name = "product_id")
    public Long productId;
    @Column(name = "product_name")
    public String productName;
    public int quantity;
    @Column(name = "unit_price")
    public BigDecimal unitPrice;
    @Column(name = "specs_text")
    public String specsText;
}
