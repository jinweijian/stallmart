package com.stallmart.user.internal.repository;

import com.stallmart.user.internal.model.AdminAccountStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "admin_account")
public class AdminAccountEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    public String account;
    @Column(name = "password_hash")
    public String passwordHash;
    @Column(name = "user_id")
    public Long userId;
    @Column(name = "store_id")
    public Long storeId;
    @Column(name = "entry_path")
    public String entryPath;
    @Enumerated(EnumType.STRING)
    public AdminAccountStatus status;
}
