-- MySQL 8+, one-time upgrade. Run with a migration account while all old application replicas are stopped.
-- Existing domain tables must already be provisioned. DDL implicitly commits in MySQL.
-- Record successful execution in your deployment migration ledger; do not run this version twice.
CREATE TABLE IF NOT EXISTS permissions (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(150) NOT NULL UNIQUE
) ENGINE=InnoDB;
CREATE TABLE IF NOT EXISTS user_roles (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
) ENGINE=InnoDB;
ALTER TABLE user_roles ADD COLUMN version BIGINT NOT NULL DEFAULT 0;
CREATE TABLE IF NOT EXISTS role_permissions (
    role_id BIGINT NOT NULL,
    permission_id BIGINT NOT NULL,
    PRIMARY KEY (role_id, permission_id),
    CONSTRAINT fk_rbac_role FOREIGN KEY (role_id) REFERENCES user_roles(id),
    CONSTRAINT fk_rbac_permission FOREIGN KEY (permission_id) REFERENCES permissions(id)
) ENGINE=InnoDB;
CREATE TABLE authorization_audit (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    actor_id BINARY(16) NOT NULL,
    action VARCHAR(40) NOT NULL,
    role_id BIGINT NOT NULL,
    role_name VARCHAR(50) NOT NULL,
    previous_version BIGINT NULL,
    resulting_version BIGINT NULL,
    before_permissions TEXT NOT NULL,
    after_permissions TEXT NOT NULL,
    reason VARCHAR(500) NOT NULL,
    occurred_at DATETIME(6) NOT NULL,
    INDEX idx_authorization_audit_role_time (role_name, occurred_at)
) ENGINE=InnoDB;
-- Audit role_id is deliberately not a foreign key: history survives deletion of a role.
-- Retain the existing user.role_id -> user_roles.id FK. Verify it exists before rollout;
-- that FK is the final protection against assignment racing with role deletion.

-- One-time immutable inventory of the original grants for reconciliation/forensics.
CREATE TABLE authorization_legacy_grants (
    role_id BIGINT NOT NULL,
    role_name VARCHAR(50) NOT NULL,
    permission_name VARCHAR(150) NOT NULL,
    archived_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6)
) ENGINE=InnoDB;
