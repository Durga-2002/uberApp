# uberApp

Spring Boot Uber-clone backend (Java 21, PostgreSQL/PostGIS, JWT auth).

## Configuration

Secrets are **not** stored in the repository. All credentials are read from environment
variables, with non-sensitive defaults (DB URL, mail host/port) in
`src/main/resources/application.properties`.

Required variables:

| Variable | Description |
| --- | --- |
| `DB_USERNAME` | PostgreSQL username |
| `DB_PASSWORD` | PostgreSQL password |
| `JWT_SECRET_KEY` | JWT signing secret, at least 32 characters / 256 bits |
| `MAIL_USERNAME` | SMTP account address |
| `MAIL_PASSWORD` | SMTP password or app password |

Optional variables with defaults: `DB_URL`, `MAIL_HOST`, `MAIL_PORT`.

### Local setup

1. Copy the template and fill in your own values:

   ```bash
   cp .env.example .env
   ```

2. Load the variables into your shell (Spring Boot does not load `.env` files by itself):

   ```bash
   set -a; source .env; set +a
   ```

3. Run the app:

   ```bash
   ./mvnw spring-boot:run
   ```

You can instead supply the same values through your IDE's run configuration, export them
in your shell profile, or set them in your deployment platform's secret store.
`.env` and `application-local.*` are gitignored and must never be committed.

If a credential was ever committed or shared in plaintext, **rotate it** (change the DB
password, regenerate the SMTP app password, and use a new JWT secret).
