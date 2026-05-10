package com.stallmart.store.internal.repository;

import com.stallmart.store.internal.model.StoreStyleStatus;
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
@Table(name = "store_style")
public class StoreStyleEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    public String name;
    public String code;
    @Enumerated(EnumType.STRING)
    public StoreStyleStatus status;
    public int version;
    @Column(name = "preview_url")
    public String previewUrl;
    @Lob
    @Column(name = "theme_json", columnDefinition = "TEXT")
    public String themeJson;
}
