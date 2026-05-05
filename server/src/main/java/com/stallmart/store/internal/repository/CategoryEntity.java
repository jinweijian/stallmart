package com.stallmart.store.internal.repository;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "category")
public class CategoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    @Column(name = "store_id")
    public Long storeId;
    public String module;
    public String name;
    @Column(name = "icon_key")
    public String iconKey;
    @Column(name = "sort_order")
    public int sortOrder;
    public String status;
}
