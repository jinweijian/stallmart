package com.stallmart.user.internal.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminAccountRepository extends JpaRepository<AdminAccountEntity, Long> {
    Optional<AdminAccountEntity> findByAccount(String account);

    Optional<AdminAccountEntity> findByUserId(Long userId);
}
