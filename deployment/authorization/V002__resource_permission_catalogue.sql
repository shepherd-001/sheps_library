-- MySQL 8+, one-time data upgrade AFTER V001. Execute as a single deployment migrator.
-- Back up roles/permissions/role_permissions first. Review legacy custom role mappings before rollout.
START TRANSACTION;
INSERT INTO authorization_legacy_grants (role_id, role_name, permission_name)
SELECT r.id, r.name, p.name FROM user_roles r
JOIN role_permissions rp ON rp.role_id = r.id JOIN permissions p ON p.id = rp.permission_id;
-- Preflight must have rejected canonical-name collisions before this normalization.
UPDATE user_roles SET name = UPPER(TRIM(name));
INSERT INTO permissions (name) VALUES
    ('book:create'),
    ('book:read'),
    ('book:update'),
    ('book:delete'),
    ('loan:borrow'),
    ('loan:return:own'),
    ('loan:read:own'),
    ('loan:read:any'),
    ('reservation:create'),
    ('reservation:read:own'),
    ('reservation:read:any'),
    ('reservation:delete:own'),
    ('user:read'),
    ('librarian:invite'),
    ('role:read'),
    ('role:create'),
    ('role:delete'),
    ('role:permissions:update')
ON DUPLICATE KEY UPDATE name = VALUES(name);
INSERT INTO user_roles (name, version) VALUES ('ADMIN', 0), ('LIBRARIAN', 0), ('MEMBER', 0)
ON DUPLICATE KEY UPDATE name = VALUES(name);

-- Translate legacy custom-role grants. Old permissions are retained for forensic comparison,
-- but application authentication no longer recognizes them.
CREATE TEMPORARY TABLE legacy_permission_mapping (old_code VARCHAR(150), new_code VARCHAR(150));
INSERT INTO legacy_permission_mapping VALUES
    ('admin.create', 'book:create'),
    ('admin.create', 'librarian:invite'),
    ('admin.create', 'role:create'),
    ('admin.read', 'book:read'),
    ('admin.read', 'loan:read:any'),
    ('admin.read', 'reservation:read:any'),
    ('admin.read', 'user:read'),
    ('admin.read', 'role:read'),
    ('admin.update', 'book:update'),
    ('admin.update', 'role:permissions:update'),
    ('admin.delete', 'book:delete'),
    ('admin.delete', 'role:delete'),
    ('librarian.read', 'book:read'),
    ('librarian.read', 'loan:read:any'),
    ('librarian.read', 'reservation:read:any'),
    ('member.create', 'loan:borrow'),
    ('member.create', 'reservation:create'),
    ('member.update', 'loan:return:own'),
    ('member.read', 'book:read'),
    ('member.read', 'loan:read:own'),
    ('member.read', 'reservation:read:own'),
    ('member.delete', 'reservation:delete:own');
INSERT INTO role_permissions (role_id, permission_id)
SELECT DISTINCT rp.role_id, target.id
FROM role_permissions rp
JOIN user_roles r ON r.id = rp.role_id
JOIN permissions source ON source.id = rp.permission_id
JOIN legacy_permission_mapping m ON m.old_code = source.name
JOIN permissions target ON target.name = m.new_code
WHERE r.name NOT IN ('ADMIN', 'LIBRARIAN', 'MEMBER')
ON DUPLICATE KEY UPDATE permission_id = VALUES(permission_id);
DROP TEMPORARY TABLE legacy_permission_mapping;
-- Remove obsolete/unknown grants only after archiving and the reviewed translation above.
-- New catalogue grants are retained. Unknown old grants need explicit operator reconciliation.
DELETE rp FROM role_permissions rp JOIN permissions p ON p.id = rp.permission_id
WHERE p.name NOT IN ('book:create', 'book:read', 'book:update', 'book:delete', 'loan:borrow', 'loan:return:own', 'loan:read:own', 'loan:read:any', 'reservation:create', 'reservation:read:own', 'reservation:read:any', 'reservation:delete:own', 'user:read', 'librarian:invite', 'role:read', 'role:create', 'role:delete', 'role:permissions:update');

-- System roles are intentionally deployment-owned. This one-time migration replaces their policy;
-- application restarts never modify them. Custom-role grants are not reset.
DELETE rp FROM role_permissions rp JOIN user_roles r ON r.id = rp.role_id
WHERE r.name IN ('ADMIN', 'LIBRARIAN', 'MEMBER');
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM user_roles r CROSS JOIN permissions p
WHERE r.name = 'ADMIN' AND p.name IN ('book:create', 'book:read', 'book:update', 'book:delete', 'loan:borrow', 'loan:return:own', 'loan:read:own', 'loan:read:any', 'reservation:create', 'reservation:read:own', 'reservation:read:any', 'reservation:delete:own', 'user:read', 'librarian:invite', 'role:read', 'role:create', 'role:delete', 'role:permissions:update');
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM user_roles r CROSS JOIN permissions p
WHERE r.name = 'LIBRARIAN' AND p.name IN ('book:read', 'loan:read:any', 'reservation:read:any');
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM user_roles r CROSS JOIN permissions p
WHERE r.name = 'MEMBER' AND p.name IN ('book:read', 'loan:borrow', 'loan:return:own', 'loan:read:own', 'reservation:create', 'reservation:read:own', 'reservation:delete:own');
UPDATE user_roles SET version = version + 1;
COMMIT;
-- Catalogue deployment changes are recorded in the migration ledger/approval history.
-- authorization_audit records authenticated runtime changes, not a fictitious migration user.
