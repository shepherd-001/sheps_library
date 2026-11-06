# Sheps Library

Spring Boot library API. The project targets Java 25 and uses Maven, MySQL, SMTP and stateless bearer authentication.

Read [AUTHORIZATION_DESIGN.md](AUTHORIZATION_DESIGN.md) for the implemented RBAC policy, API contracts and required authorization database upgrade.

Read [PRODUCTION_REVIEW.md](PRODUCTION_REVIEW.md) for the engineering assessment, before/after changes, compatibility notes and remaining production release gates.

## Configuration

Supply environment variables through your process environment or deployment secret store. This application does not automatically load a `.env` file. Ignored `application-local.yaml`/`application-dev.yaml` are machine-specific and should never contain real secrets.

Required configuration in the base profile:

| Variables | Purpose |
| --- | --- |
| DB_HOST, DB_PORT, DB_NAME, DB_USERNAME, DB_PASSWORD | Provisioned MySQL database and credentials |
| HIKARI_MAXIMUM_POOL_SIZE, HIKARI_MINIMUM_IDLE, HIKARI_CONNECTION_TIMEOUT, HIKARI_IDLE_TIMEOUT, HIKARI_MAX_LIFETIME | Pool capacity and timeouts in milliseconds; size against total replica/database connection budget |
| MAIL_HOST, MAIL_PORT, MAIL_USERNAME, MAIL_PASSWORD | SMTP configuration; STARTTLS required |
| MAIL_FROM_EMAIL, MAIL_FROM_NAME, CLIENT_URL | Sender and client links |
| CORE_POOL_SIZE, MAX_POOL_SIZE, QUEUE_CAPACITY | Bounded mail executor capacity |
| RESET_PASSWORD_EXPIRATION, EMAIL_CONFIRMATION_EXPIRATION, LIBRARIAN_INVITATION_EXPIRATION | One-time token lifetimes in seconds |
| JWT_SECRET_KEY, JWT_EXPIRATION, JWT_REFRESH_EXPIRATION | High-entropy HMAC secret (at least 32 UTF-8 bytes), access/refresh lifetimes in seconds |

Optional: `PORT` (8080), `DDL_AUTO` (validate), `ENABLE_SPRING_DOC`/`ENABLE_SWAGGER_UI` (false), `ADMIN_BOOTSTRAP_ENABLED` (false). Configure `app.security.allowed-origins` through deployment configuration for the intended browser clients. Never use automatic DDL updates for a production deployment.

## Build and test

Use a working JDK 25 installation and set JAVA_HOME explicitly if the system Java launcher is a shim.

```powershell
.\mvnw.cmd '-Dtest=*ServiceImplTest,JwtUtilsTest' test
.\mvnw.cmd clean test
.\mvnw.cmd package
```

The targeted command runs the new isolated regression tests. The full test command runs the Spring context and authorization integration tests using the isolated in-memory H2 `test` profile; it does not use a real SMTP server or your MySQL data. No production database is provisioned or migrated automatically. Before launching a server, supply configuration and provision a compatible schema. Apply the versioned scripts described in AUTHORIZATION_DESIGN.md before running this version against an existing MySQL database. MySQL migration verification remains a deployment release gate.

```powershell
.\mvnw.cmd spring-boot:run
```

Rotate any secrets previously stored or shared in local configuration. The code changes do not revoke old credentials.
