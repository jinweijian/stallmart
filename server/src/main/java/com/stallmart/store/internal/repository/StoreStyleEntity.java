package com.stallmart.store.internal.repository;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;

@Entity
@Table(name = "store_style")
public class StoreStyleEntity {
    @Id
    public Long id;
    public String name;
    public String code;
    public String status;
    public int version;
    @Column(name = "preview_url")
    public String previewUrl;
    @Lob
    @Column(name = "theme_json", columnDefinition = "TEXT")
    public String themeJson;
}
