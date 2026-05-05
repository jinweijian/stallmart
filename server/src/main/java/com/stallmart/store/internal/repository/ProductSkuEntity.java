package com.stallmart.store.internal.repository;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "product_sku")
public class ProductSkuEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    @Column(name = "product_id")
    public Long productId;
    @Lob
    @Column(name = "spec_values_json", columnDefinition = "TEXT")
    public String specValuesJson;
    public BigDecimal price;
    public int stock;
    public String status;
}
