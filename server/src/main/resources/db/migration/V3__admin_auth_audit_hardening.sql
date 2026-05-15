ALTER TABLE admin_account
    ADD COLUMN password_salt VARCHAR(120);

UPDATE admin_account
SET password_salt = 'platform-salt-uB9zR5mKx2Yq2026',
    password_hash = '$2a$10$CQttB85yHEoQmCQYOXm/zuD9YwHMxsJMtZEOgolCUktgWVgYQAVbO'
WHERE account = 'platform';

UPDATE admin_account
SET password_salt = 'vendor-salt-Lp4nT8cQw6Hs2026',
    password_hash = '$2a$10$W8hTsNeR4IH.WPH.AEfd0.wJVVB7RBfb0ptZcghxXd5u0wplOaHli'
WHERE account = 'vendor';

CREATE TABLE admin_operation_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    scope VARCHAR(20) NOT NULL,
    store_id BIGINT,
    actor_user_id BIGINT,
    actor_account VARCHAR(80),
    actor_role VARCHAR(20),
    action VARCHAR(80) NOT NULL,
    resource_type VARCHAR(80) NOT NULL,
    resource_id VARCHAR(80),
    description VARCHAR(300) NOT NULL,
    result VARCHAR(20) NOT NULL,
    ip_address VARCHAR(80),
    user_agent VARCHAR(300),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_admin_operation_log_scope_created ON admin_operation_log(scope, created_at);
CREATE INDEX idx_admin_operation_log_store_created ON admin_operation_log(store_id, created_at);
