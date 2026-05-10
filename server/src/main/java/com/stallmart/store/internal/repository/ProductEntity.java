package com.stallmart.store.internal.repository;

import com.stallmart.store.internal.model.ProductStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;

@Entity
@Table(name = "product")
public class ProductEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    @Column(name = "store_id")
    public Long storeId;
    @Column(name = "category_id")
    public Long categoryId;
    public String name;
    public String description;
    @Column(name = "main_image_url")
    public String mainImageUrl;
    @Enumerated(EnumType.STRING)
    public ProductStatus status;
    @Column(name = "sort_order")
    public int sortOrder;
    @Lob
    @Column(name = "spec_ids_json", columnDefinition = "TEXT")
    public String specIdsJson;
}
