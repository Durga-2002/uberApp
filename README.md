# uberApp

Spring Boot application for an Uber clone. The application uses Java 21, PostgreSQL/PostGIS, and JWT authentication.

## Configuration

Do not store secrets in the repository.
The application reads all credentials from environment variables.
The file `src/main/resources/application.properties` contains non-sensitive defaults, such as the DB URL, the mail host, and the mail port.

Required variables:

| Variable | Description |
| --- | --- |
| `DB_USERNAME` | PostgreSQL user name |
| `DB_PASSWORD` | PostgreSQL password |
| `JWT_SECRET_KEY` | Secret that signs JWTs. Use 32 characters or more (256 bits). |
| `MAIL_USERNAME` | SMTP account address |
| `MAIL_PASSWORD` | SMTP password or application password |

Optional variables: `DB_URL`, `MAIL_HOST`, and `MAIL_PORT`. Each has a default value.

### Local setup

1. Copy the template file.

   ```bash
   cp .env.example .env
   ```

2. Enter the required values in `.env`.
3. Load the variables in the shell. Spring Boot does not load `.env` files.

   ```bash
   set -a; source .env; set +a
   ```

4. Start the application.

   ```bash
   ./mvnw spring-boot:run
   ```

To supply the values in another way, use the run configuration of the IDE, the shell profile, or the secret store of the deployment platform.

List `.env` and `application-local.*` in `.gitignore`. Do not commit these files.

If a credential was committed or shared in plain text, change it. Change the DB password, generate a new SMTP application password, and use a new JWT secret.
