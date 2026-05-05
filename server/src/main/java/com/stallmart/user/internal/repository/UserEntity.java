package com.stallmart.user.internal.repository;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    @Column(name = "open_id")
    public String openId;
    public String nickname;
    @Column(name = "avatar_url")
    public String avatarUrl;
    public String phone;
    @Column(name = "has_phone")
    public boolean hasPhone;
    public String role;
    public String status;
}
