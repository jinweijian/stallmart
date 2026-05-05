package com.stallmart.store.internal.repository;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;

@Entity
@Table(name = "product_spec")
public class ProductSpecEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    @Column(name = "style_id")
    public Long styleId;
    public String name;
    @Column(name = "spec_type")
    public String specType;
    @Column(name = "is_required")
    public boolean required;
    @Lob
    @Column(name = "options_json", columnDefinition = "TEXT")
    public String optionsJson;
}
