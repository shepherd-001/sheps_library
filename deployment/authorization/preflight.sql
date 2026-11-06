-- Run BEFORE V001/V002 against the target database. Resolve every result before proceeding.
-- Names that would collide after normalization:
SELECT UPPER(TRIM(name)) AS normalized, COUNT(*) AS copies
FROM user_roles GROUP BY UPPER(TRIM(name)) HAVING COUNT(*) > 1;
-- Names incompatible with the new API:
SELECT id, name FROM user_roles WHERE UPPER(TRIM(name)) NOT REGEXP '^[A-Z][A-Z0-9_]{0,49}$';
-- Inspect existing user-role FK. It must prevent dangling user.role_id values.
SELECT table_name, column_name, constraint_name, referenced_table_name
FROM information_schema.key_column_usage
WHERE table_schema = DATABASE() AND referenced_table_name = 'user_roles';
-- Export this inventory for approval/reconciliation and for rollback planning:
SELECT r.id, r.name, p.name AS permission_name
FROM user_roles r LEFT JOIN role_permissions rp ON rp.role_id = r.id
LEFT JOIN permissions p ON p.id = rp.permission_id ORDER BY r.name, p.name;
