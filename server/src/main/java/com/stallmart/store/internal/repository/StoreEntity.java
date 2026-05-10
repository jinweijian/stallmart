package com.stallmart.store.internal.repository;

import com.stallmart.store.internal.model.StoreStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "store")
public class StoreEntity {
    @Id
    public Long id;
    @Column(name = "owner_id")
    public Long ownerId;
    @Column(name = "style_id")
    public Long styleId;
    @Column(name = "app_id")
    public String appId;
    public String name;
    public String category;
    public String description;
    @Column(name = "logo_url")
    public String logoUrl;
    @Column(name = "cover_url")
    public String coverUrl;
    @Column(name = "qr_code")
    public String qrCode;
    public String address;
    @Enumerated(EnumType.STRING)
    public StoreStatus status;
}
