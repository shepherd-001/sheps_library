# Role and permission design

This implementation uses application-owned capability definitions, migration-owned system roles and administrator-managed custom roles. It is a single-tenant RBAC model. It keeps the existing one-role-per-user account model; a user-role reassignment API is outside this change.

## Authority catalogue and system roles

`PermissionCatalog` is the only recognized capability catalogue. Arbitrary permission creation/deletion and business-service cache controls have been removed. Unknown or legacy database permission codes do not become Spring Security authorities. Descriptions and codes are exposed through `GET /api/v1/admin/permissions` with `role:read`.

| System role | Policy |
| --- | --- |
| ADMIN | All catalogue capabilities, including role administration and member operations |
| LIBRARIAN | book:read, loan:read:any, reservation:read:any |
| MEMBER | book:read, loan:borrow, loan:return:own, loan:read:own, reservation:create, reservation:read:own, reservation:delete:own |

System roles cannot be created, deleted or edited through runtime administrative services. A reviewed migration changes their policy. Startup only verifies the catalogue and system-role policy; it never repairs or resets custom-role grants. A mismatch stops startup rather than silently granting or revoking permissions.

Capabilities now describe resources and actions rather than role names. Book, user, invitation, loan and reservation controllers use the new catalogue. Owning a capability such as `loan:return:own` still requires the existing object-ownership checks; the permission name itself is not an ownership check.

## Custom role administration

- Role names are canonical uppercase ASCII identifiers of 1-50 characters. The same limit is enforced in DTO validation, service normalization and schema.
- New custom roles start with no capabilities.
- Every mutation requires the corresponding capability inside the service, protecting callers that bypass HTTP controllers. Controllers enforce the same capabilities.
- An actor cannot edit/delete their own role or mutate a protected system role.
- An actor must hold every capability in both the existing and requested role policy. This prevents both privilege escalation and stripping permissions from a role outside the operator's delegation boundary.
- Only an ADMIN-role actor can delegate `role:*` administration capabilities. A delegated custom administrator can manage ordinary roles within their capability boundary, but cannot create another role administrator.
- Permission replacement validates codes, rejects normalized duplicates and performs one batch permission lookup. Arbitrary, blank or unknown capabilities are rejected.
- Replacement means complete replacement. An explicit empty list revokes all permissions. Missing/null lists are rejected. An unchanged replacement is a no-op: no artificial version increment or change audit record.
- Updates and deletion require `expectedVersion`. `@Version` also detects overlapping database writes; stale requests return HTTP 409. Callers must reload and review, not blindly retry a policy overwrite.
- Assigned roles cannot be deleted. An explicit check provides a domain error, and the user-role database foreign key must remain in place to handle races with account provisioning.
- Reads return immutable DTO snapshots; JPA entities remain internal to account-provisioning lookups.

## API

All paths below are under `/api/v1/admin`.

| Operation | Request | Required capability |
| --- | --- | --- |
| GET /permissions | None | role:read |
| GET /roles | page, size, sort (id/name), direction | role:read |
| GET /roles/{name} | Role name | role:read |
| POST /roles | name, reason | role:create |
| PUT /roles/permissions | roleName, permissionNames, expectedVersion, reason | role:permissions:update |
| DELETE /delete-role | roleName, expectedVersion, reason query parameters | role:delete |

`POST /add-role` and `PUT /assign-permission-to-role` remain URL aliases, but their request contracts now require the new fields. The former assignment operation is explicitly a full replacement. Delete returns 204. Create returns 201. Modification responses include the role ID, canonical name, current version, systemRole flag, and permissionNames.

Example creation:

```json
{"name":"HELPDESK","reason":"Approved support team access"}
```

Read the returned version before replacement:

```json
{
  "roleName":"HELPDESK",
  "permissionNames":["book:read","loan:read:any"],
  "expectedVersion":0,
  "reason":"Approved helpdesk read access"
}
```

Unknown capabilities/invalid input produce 400; denied operations produce 403; stale versions, assigned-role deletion and database integrity conflicts produce 409.

## Consistency, query cost and auditing

There are no authorization caches or cached mutable JPA entities. The catalogue itself is immutable code metadata. Authentication reloads the user's role and permissions for each request; after a committed change, the next request using the same access token sees the new authorities. In-flight requests retain their already-established authentication snapshot. Strict cancellation of in-flight work would need an additional policy/version check at the protected operation.

Role mutation and audit insertion use the same database transaction. Failure to store the audit record rolls back the change. Audit rows contain actor UUID, operation, role ID/name, before/after permission codes, previous/resulting versions, reason and UTC timestamp. Role identifiers are scalar historical references, so deleting a role does not erase its history. Permission snapshots are sorted newline-separated catalogue codes; codes cannot contain newlines. No token, password or email address is stored in these audit records.

Audit entities are immutable and the repository exposes insertion only. This is application-level append-only behavior, not tamper-proof storage. Use a restricted database account (INSERT, and separately controlled audit reading; no UPDATE/DELETE on authorization_audit), controlled retention/export and operator access. Deployment catalogue changes belong in migration approval/history, not in runtime records with a fabricated user. Denied attempts still require centralized security-log monitoring; this audit table records successful committed mutations.

Role lists use bounded database pagination followed by one batch association fetch, avoiding collection-fetch pagination in memory. Permission replacement uses one batch lookup. No throughput or latency improvement is claimed without load testing.

## Deployment and compatibility

**Do not roll this out as a mixed old/new cluster.** Old instances use different authorities and their startup seeders overwrite policies. Stop/drain all old replicas, back up authorization tables and use one migration account/process.

1. Inspect `deployment/authorization/preflight.sql`; resolve canonical-name collisions, incompatible names and unexpected grants. Confirm the user-role foreign key exists and archive/approve the current assignments.
2. Apply `V001__authorization_schema.sql` once to the provisioned MySQL 8+ domain database. It adds the role version and immutable audit storage. MySQL DDL implicitly commits: use a tested backup/recovery plan, not an assumed transactional DDL rollback.
3. Apply `V002__resource_permission_catalogue.sql` once. It provisions the resource catalogue, intentionally replaces deployment-owned system policies, archives the old grants in authorization_legacy_grants, translates known legacy custom grants, removes obsolete grant associations and preserves custom-role definitions. Review the actual SQL and old/new inventory before executing. Invalid/unknown legacy grants need explicit migration review; the application will not recognize old permission codes.
4. Record migration versions/checksums and the approving operator in your deployment migration ledger. These scripts are scoped RBAC upgrades, not a full-domain schema baseline and not automatically executed by the application. Do not rerun them on each restart.
5. Start new replicas with `ddl-auto=validate` and the catalogue verifier enabled (default). Verify account login, representative allow/deny cases and next-request revocation.
6. Grant the runtime DB account the required domain/authorization DML, including audit INSERT, but no schema-change privileges or audit modification/deletion privileges.

Existing JWTs can continue to be used because authorities are reloaded from the database. Legacy role-prefixed capabilities no longer authorize endpoints. ADMIN deliberately gains the entire resource catalogue so it can delegate capabilities it actually holds. Legacy authorization bugs are not preserved: `admin.create` no longer implicitly allows role deletion, and `member.read` no longer allows reservation deletion. Permissions must be reconciled accordingly.

Startup verification can be disabled with `app.authorization.verify-catalogue=false` for isolated tests only; production deployments should leave it enabled. This change does not provision a running database or execute migrations against your data.

## Verification

The isolated `test` profile uses in-memory H2 with no real SMTP/database credentials. Integration tests cover HTTP authentication/authorization/validation, same-JWT revocation, actual Hibernate version increments and concurrent updates, audit rollback, protected roles, assigned-role deletion, delegation bounds and immutable responses. The full application context is exercised as well. Final verification: **28 tests passed, 0 failures, 0 errors, 0 skipped**. This includes 15 authorization integration tests, two deployment/endpoint contract checks, the application context test and the 10 existing regression tests. A clean build passed before the final two contract checks were added; the final full `mvn -o test` run also passed. `git diff --check` passed. Execution used the installed Maven 3.9.9 and JDK 26.0.1 with Java 25 compilation target; a production JDK 25 run remains advisable. Existing unrelated Lombok/MapStruct and Mockito agent warnings remain.

The MySQL scripts were not executed against a server. Docker CLI is installed, but its Linux engine pipe is absent (daemon unavailable), so no disposable MySQL instance was available in this session. The contract tests check catalogue/policy agreement across SQL and Java; they do not validate MySQL SQL execution.

H2 is not MySQL qualification. Apply the supplied migrations on a disposable production-version MySQL database and repeat concurrency, foreign-key race and rollback cases before deploying. Load testing, cross-service/tenant policies, approval workflows/MFA for administrators and audit export/retention remain deployment/business decisions.

The implementation follows the mechanisms described in [Spring transaction guidance](https://docs.spring.io/spring-data/jpa/reference/jpa/transactions.html) and [OWASP authorization guidance](https://cheatsheetseries.owasp.org/cheatsheets/Authorization_Cheat_Sheet.html).

## Former implementation versus this implementation

| Former | New |
| --- | --- |
| Broad role-prefixed CRUD authorities | Explicit resource/action capability catalogue used by all protected controllers |
| Arbitrary permission CRUD | Read-only catalogue; capability changes require code and migration review |
| Runtime seeders overwrite default role policy | One-time deployment scripts; startup performs read-only verification |
| System and custom roles mutable through the same operations | Protected ADMIN/LIBRARIAN/MEMBER; custom roles have controlled runtime management |
| Operators can replace grants without a delegation boundary | Service checks capability, protected targets, self-editing and both old/new grant scope; only ADMIN can delegate role administration |
| Ambiguous permission assignment | Explicit full replacement with required reason; empty list deliberately revokes all |
| No stale-write protection | Client expectedVersion plus Hibernate @Version; conflicts return 409 |
| Multiple permission lookups | One batch lookup with complete catalogue validation |
| Mutable local entity caches | No authorization cache; database-backed next-request revocation |
| Generic log messages | Committed actor/before/after/version/reason audit rows in the same transaction |
| Role delete relies only on database errors | Assigned-role check, system-role protection, expected version and FK protection |
| Entity responses and mismatched validation lengths | Immutable DTOs and consistent 50-character role-name validation |
| Environment-dependent startup test | Isolated H2 profile, HTTP/security/concurrency/rollback tests and catalogue contract checks |
