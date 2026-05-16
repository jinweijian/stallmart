package com.stallmart.management.internal.repository;

import com.stallmart.management.internal.model.OperationLogResult;
import com.stallmart.management.internal.model.OperationLogScope;
import com.stallmart.user.internal.model.UserRole;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "admin_operation_log")
public class OperationLogEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    @Enumerated(EnumType.STRING)
    public OperationLogScope scope;
    @Column(name = "store_id")
    public Long storeId;
    @Column(name = "actor_user_id")
    public Long actorUserId;
    @Column(name = "actor_account")
    public String actorAccount;
    @Enumerated(EnumType.STRING)
    @Column(name = "actor_role")
    public UserRole actorRole;
    public String action;
    @Column(name = "resource_type")
    public String resourceType;
    @Column(name = "resource_id")
    public String resourceId;
    public String description;
    @Enumerated(EnumType.STRING)
    public OperationLogResult result;
    @Column(name = "ip_address")
    public String ipAddress;
    @Column(name = "user_agent")
    public String userAgent;
    @Column(name = "created_at")
    public Instant createdAt;
}
