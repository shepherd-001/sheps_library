> Update: the role/permission findings and the earlier missing-MAIL_HOST test limitation below describe the initial review. The subsequent RBAC redesign removes the remaining authorization caches, runtime seeders and broad role-prefixed capabilities, adds transactional change auditing and version checks, and supplies an isolated test profile. See [AUTHORIZATION_DESIGN.md](AUTHORIZATION_DESIGN.md) for the current authorization design and rollout requirements. The final full suite now passes all 28 tests. Other release gates remain applicable.

# Engineering and production-readiness review

## Verdict

The original code follows several sound engineering practices, but only partially. It has controller/service/repository separation, constructor injection, DTOs and MapStruct, Jakarta validation, centralized errors, BCrypt password hashing, role permissions, bounded pagination, and transactional borrowing. These are a good foundation, not proof of production readiness.

The original version is **not production grade**. The revised version fixes concrete correctness and security defects, but **is not yet production certified**. No throughput, availability, recovery, or multi-instance consistency claim has been established through deployment tests. This review inspected the checked-out source and configuration; it is not a penetration test or dependency vulnerability audit.

## Implemented changes: previous behavior and revised behavior

| Area | Previous behavior | Revised behavior and reason |
| --- | --- | --- |
| Loan authorization | Any member with update permission could return any transaction UUID. | The controller supplies the authenticated principal and the service checks ownership before changing data. Prevents object-level authorization bypass. |
| Repeated return | An old returned loan could be returned again, making a book available even after someone else borrowed it. | Only a BORROW_BOOK transaction may transition to RETURN_BOOK. Repeated return is rejected before touching the book. |
| Concurrency and atomicity | Borrowing read availability without a lock. Returning saved the book and transaction in separate repository transactions. | Borrowing locks the book; returning locks the loan and book inside one transaction. Metadata updates and reservations use the same book lock. Concurrent service calls serialize decisions; partial return updates roll back together. MySQL concurrency and rollback integration tests remain required. |
| Reservation privacy | Any reader could fetch another member's reservation by UUID. | The service checks ownership, allowing librarian/admin read authorities to inspect reservations. |
| API bearer tokens | The lookup accepted either access or refresh token; a JPQL OR expression also bypassed the token-type condition on one branch. | API authentication looks up only the stored access token and verifies the access claim. The shared lookup now groups its OR expression correctly. |
| One-time tokens | Revoked/expired flags were ignored; simultaneous consumption was not serialized. | Validation rejects flagged tokens and locks the token row inside the caller's consuming transaction. Verification, reset, and librarian activation consume tokens atomically. |
| Librarian activation | The already-created-password condition was inverted and email verification stayed false. Multiple saves lacked an enclosing transaction. | Rejects existing passwords/enabled accounts, verifies the invited email, and performs activation/token consumption in one transaction. |
| Mutable caches | Updates invalidated individual keys while ISBN, filtered, paginated, user, and other-instance results remained stale. | Removed caches from book, loan, reservation, and user read methods. The database is authoritative. This adds read traffic; measure before reintroducing caching with an explicit consistency contract. Role/permission entity caches remain a separate design concern. |
| Filtering | Author predicate used the supplied title. | Author predicate uses the author field, and omitted optional filters are handled safely. |
| Input/date correctness | Borrow fields had no null validation; missing dates could trigger an exception. Loan limits depended on the machine timezone. Invalid datetime strings silently became null. | Required UUID/date and future-date validation; service also rejects missing/past dates. Loan-month arithmetic uses UTC. Invalid datetime text fails parsing. |
| Query cost | Transaction DTO mapping could issue additional user/book queries per result. | Entity graphs fetch the to-one associations needed by transaction list responses. Query count and execution plans still need measurement against MySQL. |
| Persistence boundaries | Open-session-in-view could allow hidden database access during response rendering. | Disabled it and placed mutable-resource read services within read-only transactions, keeping DTO mapping within the service boundary. |
| Authentication lookup | A method named IgnoreCase used a case-sensitive explicit JPQL equality. | Uses lower-case comparison and fetches role/permissions for authentication. |
| Error semantics | Method authorization failure returned 401; bearer failures exposed exception details. Uncaught errors omitted stack traces. | Permission denial returns 403. Bearer errors have generic client text. Server-side unexpected errors include stack traces. Data and lock conflicts return generic 409 responses. |
| Bootstrap | Startup always attempted admin creation. | Admin creation is explicitly opt-in and requires supplied credentials, with a minimum bootstrap password length. Existing admins are not modified. |
| Configuration | Base config used automatic schema update, database auto-creation, enabled documentation through mandatory external switches, and ambiguous cache expiry units. | Base config defaults to schema validation, removes database auto-creation, defaults documentation off, and uses an explicit 10m cache duration. Operators must provision and migrate the database. |
| Local secrets | Ignored local/dev YAML contained literal database, SMTP, admin and JWT secrets. These files are not tracked in Git. | Replaced secret values with environment references in those local files. Their changes do not appear in a patch/commit because the files are ignored. Rotate any previously exposed values; removing them does not revoke them. |
| Shutdown | Mail executor had no explicit drain period. | Graceful server shutdown and 30-second lifecycle/mail draining are explicit. This helps normal restarts but does not make email durable after a process crash. |
| Build/mapping | Book creation had an invalid nested MapStruct target. ISBN save retries could misclassify arbitrary constraint failures and cannot recover inside a rollback-only transaction. | Removed the invalid mapping; flushes the single insert and lets constraint failure roll back cleanly as 409. No unsafe retry within a failed transaction. |
| Tests | One environment-dependent context smoke test; controller test entirely commented out. | Added focused regression tests for security and loan invariants. Tests configure the subclass mock maker for non-final mocks; Mockito initialization still emitted an agent-attachment warning on JDK 26. The original context test remains and still needs a provisioned integration environment. |

## Non-functional requirements and remaining release gates

These are **proposed acceptance criteria**, not measurements or agreed business requirements. Confirm them with the product/operator before a release.

| Quality | Proposed requirement | Remaining work / verification |
| --- | --- | --- |
| Security | No cross-user read/write access, no refresh/one-time token accepted as an access token; secrets supplied externally. | Add HTTP-level permission/ownership tests, abuse throttling for signup/login/reset, key rotation procedure, dependency and image scanning, and threat modeling. Verify TLS and trusted proxy configuration. CSRF-disabled stateless bearer auth assumes credentials are not automatically sent in cookies. |
| Correctness | At most one active borrower per logical book; all related writes commit together. | Run competing MySQL sessions for borrow/return/reserve, forced rollback, repeat-return-after-reborrow, and double-token-consumption tests. Set and verify database lock-wait limits. Current application-level locks do not protect arbitrary SQL writers. |
| Performance | Initially propose p95 below 300 ms and p99 below 1 s at 50 concurrent clients, excluding email delivery. | Establish a representative dataset and workload, measure database/query counts, connection-pool saturation, lock waits, CPU/memory, and error rate. No performance speedup is claimed by this patch. |
| Availability | Initially propose 99.9% monthly availability. | Add Actuator readiness/liveness with restricted exposure, metrics, alerting, deployment health checks, redundancy, connection/resource budgets and an incident runbook. Graceful shutdown must fit the platform termination grace period. |
| Reliability | Account/token writes survive crashes; notification jobs eventually complete without premature sends. | Implement a transactional outbox and idempotent email worker with bounded retries and dead-letter handling. Current async email may run before commit or be lost at process exit; executor draining is insufficient for crash safety. |
| Scalability | Multiple instances preserve authorization and inventory invariants. | Load-test row-lock contention and database capacity. Authentication still does database lookups per request. Review cached role/permission entities and seed operations across instances. A shared cache alone does not solve invalidation races. |
| Data integrity | Schema changes are versioned, reviewed and reversible through a defined recovery procedure. | Add Flyway/Liquibase migrations after inspecting the actual database, baseline existing installations safely, and test upgrades on a production-like snapshot. `ddl-auto=validate` intentionally refuses an unprovisioned/incompatible schema. No live database was changed. |
| Domain integrity | Reservation queues and physical copies follow documented rules. | Reservation currently uses OneToOne book association, effectively limiting a book to one reservation, while service checks imply uniqueness per user/book. Decide semantics, migrate to ManyToOne plus an appropriate unique constraint if queues are intended. BookLoan/BookCopy coexist with Transaction and a single Book.available flag; unify the inventory model before supporting multiple copies. |
| Maintainability | Responsibilities are clear, public contracts documented and automated checks protect invariants. | Remove unused/commented implementations, correct package typos, define authorization policies centrally, add meaningful CI checks, and separate account/inventory/notification concerns where needed. Service interfaces alone do not establish SOLID compliance. Remaining mapper warnings should become explicit ignores or mappings before enforcing strict unmapped-target failure. |
| Observability/privacy | Failures can be correlated without storing credentials or unnecessary personal data. | Add request/trace IDs and structured logs, metrics and tracing; reduce email-address logging and define retention/access policies. AuditLoggingFilter and much of audit machinery are commented out; JPA createdBy/createdAt is not a durable business/security audit trail. |
| Recovery | Initially propose RPO <=15 min and RTO <=60 min. | Configure encrypted backups and point-in-time recovery, then demonstrate restoration and application validation. Document ownership and retention. |
| Testability | A clean checkout can run unit tests independently; integration tests run against disposable MySQL. | Add a reproducible MySQL integration profile/Testcontainers, schema migrations and a CI JDK matching Java 25. The environment here has JDK 26; do not treat this as runtime qualification for Java 25. |

## Compatibility and deployment notes

- HTTP paths and payloads are retained. Return-book and reservation-by-ID service methods now require an authenticated principal; internal callers must supply it.
- Unauthorized returns/reservation reads now fail with 403; a repeated return fails as a business validation error. Refresh tokens no longer work as API credentials.
- Default admin seeding is disabled. Enable `ADMIN_BOOTSTRAP_ENABLED=true` only for deliberate provisioning, supply `ADMIN_EMAIL`/`ADMIN_PASSWORD`, and turn it off afterward.
- Documentation defaults off unless explicitly enabled. Local/dev profiles still deliberately enable it.
- Existing deployments must have a compatible schema before starting with validation. The corrected physical index column references must be checked during schema migration. No migration has been guessed from an unknown live schema.
- Due-date arithmetic now uses UTC consistently; clients still submit an explicit offset and receive request-timezone formatting.
- Base configuration disables open-session-in-view. Any additional code that serializes lazy JPA entities outside transactions must be reviewed; admin role endpoints still expose entities and should be replaced with DTOs.

## Reference guidance

The locking and operational changes follow the official guidance for [Spring Data JPA locking](https://docs.spring.io/spring-data/jpa/reference/jpa/locking.html), [transaction boundaries](https://docs.spring.io/spring-data/jpa/reference/jpa/transactions.html), [Spring Boot database initialization](https://docs.spring.io/spring-boot/how-to/data-initialization.html), and [graceful shutdown](https://docs.spring.io/spring-boot/reference/web/graceful-shutdown.html). These references describe mechanisms; they do not certify this application.

## Validation results

- Maven compilation and 10 isolated regression tests passed: 0 failures, 0 errors, 0 skipped.
- Command: `mvn -o "-Dtest=*ServiceImplTest,JwtUtilsTest" test`, using the already installed Maven 3.9.9 distribution and an explicit JDK 26 JAVA_HOME. The project still targets Java 25.
- The sandboxed JVM stalled at test initialization; the successful run used approved execution outside the sandbox.
- `git diff --check` passed.
- The existing Spring context smoke test failed: 1 error, because the required `MAIL_HOST` placeholder is not supplied. Startup did not reach database validation. The full suite is therefore not green in the current environment; provide an isolated integration configuration and compatible database before release.
- MySQL locking/rollback behavior, HTTP integration, schema upgrades, load, failure recovery, SMTP delivery, and a dependency vulnerability scan have not been verified. Existing Lombok/MapStruct warnings remain.

