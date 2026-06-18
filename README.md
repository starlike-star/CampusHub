# CampusHub

CampusHub is a Java Web campus service platform built for coursework demonstration and acceptance testing. It integrates campus community posts, second-hand trading, lost-and-found services, activity registration, announcements, notifications, private messages, global search, daily check-ins, user levels, and administrator moderation.

Chinese documentation is available in [README-ZN.md](README-ZN.md).

## Highlights

- Traditional Java Web architecture: JSP, Servlet, Service, DAO, JDBC, and MySQL.
- Student-facing workflows: registration, login, posts, comments, likes, favorites, reports, goods trading, simulated payment, lost-and-found claims, activity registration, notifications, and private messages.
- Administrator workflows: dashboard statistics, user management, content moderation, announcement management, and report handling.
- MySQL scripts delivered for both formal handoff restoration and clean development initialization.
- External upload storage so user-uploaded images survive rebuilds and Tomcat redeployments.
- Unit tests for services and utility classes.

## Feature Overview

### Student System

- Registration, CAPTCHA login, logout, and Remember Me authentication.
- Campus home feed, announcements, activity recommendations, and lost-and-found updates.
- Campus square, post publishing, comments, likes, favorites, and reports.
- Second-hand goods publishing, filtering, editing, favorites, status management, seller messaging, and simulated QR payment.
- Lost-and-found publishing, claim requests, and claim handling.
- Activity publishing, registration, cancellation, deadline checks, and capacity limits.
- Daily check-ins, consecutive-day tracking, experience points, and user levels.
- Personal profile, user-generated content, purchase history, and public user pages.
- Notifications, unread counters, one-to-one private messages, and global search.
- Account soft deletion with password confirmation, anonymization, and related-data cleanup.

![CampusHub frontend feature map](docs/images/frontend-feature-map.png)

### Administration System

- Platform statistics dashboard.
- User activation, suspension, status inspection, and password reset.
- Post, goods, lost-and-found, and activity moderation.
- Announcement publishing, editing, visibility control, and pinning.
- Report review, approval, rejection, and reported-content moderation.
- Administrator authorization filter and report notifications.

![CampusHub administration feature map](docs/images/admin-feature-map.png)

## Technology Stack

| Area | Technology |
| --- | --- |
| Backend | Java 21, Servlet 4.0, JSP, JDBC |
| Frontend | HTML, CSS, JavaScript, Fetch API |
| Database | MySQL 8 |
| Web Container | Apache Tomcat 9 |
| Build Tool | Maven |
| Security | BCrypt, Session, Cookie, CAPTCHA, Remember Me token |
| QR Code | ZXing |
| Testing | JUnit 5 |

CampusHub uses the `javax.servlet` API and should be deployed on Tomcat 9. Tomcat 10+ uses `jakarta.servlet` by default and is not directly compatible.

## Architecture

The backend follows a `Servlet -> Service -> DAO -> Model` structure. JSP pages render server-side views, while JavaScript and Fetch API handle partial asynchronous updates.

![System architecture](docs/figures/figure-2-1-system-architecture.png)

Request processing is centralized through Tomcat mappings, filters, Servlet controllers, services, DAO classes, and MySQL.

![Request flow](docs/figures/figure-2-2-request-flow.png)

## Database Model

The `users` table is the core account table. Business tables are organized around posts, comments, goods, orders, lost-and-found records, claim requests, activities, registrations, messages, reports, and notices.

![Database relationship diagram](docs/figures/figure-3-1-database-er.png)

The complete schema contains the following main tables:

```text
users, categories, posts, comments, likes, favorites, goods,
lost_found, claim_requests, activities, activity_registrations,
notices, checkins, messages, reports, remember_tokens,
private_conversations, private_messages, user_experience_logs,
goods_orders, account_cancel_logs
```

## Project Structure

```text
CampusHub/
|-- database/
|   |-- demo-data.sql              # Optional demo content for an existing local database
|   `-- migrations/                # Historical incremental migration notes
|-- docs/
|   |-- acceptance-checklist.md     # Manual acceptance checklist
|   |-- figures/                   # Report figures
|   `-- images/                    # README diagrams
|-- src/
|   |-- main/
|   |   |-- java/cn/campushub/
|   |   |   |-- config/            # Database configuration
|   |   |   |-- constant/          # Shared constants
|   |   |   |-- dao/               # DAO interfaces and JDBC implementations
|   |   |   |-- filter/            # Encoding and authorization filters
|   |   |   |-- model/             # Entities, VOs, and result models
|   |   |   |-- service/           # Business services
|   |   |   |-- servlet/           # Request controllers
|   |   |   `-- util/              # Validation, password, JSON, QR, upload utilities
|   |   |-- resources/
|   |   |   |-- database.properties
|   |   |   `-- database/
|   |   |       |-- campushub.sql    # Formal handoff dump exported from the current MySQL database
|   |   |       `-- schema.sql      # Clean development database schema
|   |   `-- webapp/
|   |       |-- WEB-INF/views/      # JSP pages and fragments
|   |       |-- css/
|   |       |-- js/
|   |       `-- images/
|   `-- test/java/cn/campushub/     # Unit tests
|-- database-schema.md
|-- pom.xml
|-- README.md
`-- README-ZN.md
```

## Requirements

- JDK 21 or later
- Maven 3.9+
- MySQL 8.0+
- Apache Tomcat 9.0+
- IntelliJ IDEA is optional but recommended for local Tomcat deployment

## Database Setup

For formal handoff, restore the database from the exported MySQL dump:

```sql
CREATE DATABASE IF NOT EXISTS campushub
  DEFAULT CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;
USE campushub;
SOURCE C:/Users/1/IdeaProjects/CampusHub/src/main/resources/database/campushub.sql;
```

`src/main/resources/database/campushub.sql` is the recommended script for final delivery and demonstration. It was exported from the current local MySQL database and contains the current table structure plus demonstration records.

The dump does not create the database by itself, so run `CREATE DATABASE` and `USE campushub` before `SOURCE`.

For development from an empty schema without the exported demonstration state, use:

```sql
SOURCE C:/Users/1/IdeaProjects/CampusHub/src/main/resources/database/schema.sql;
```

`src/main/resources/database/schema.sql` creates the database, all current business tables, indexes, foreign keys, and base category data used by the application.

Files under `database/migrations/` are retained as historical incremental records for existing databases. For formal handoff, use `campushub.sql`; for clean development initialization, use `schema.sql`.

For an existing local database that already contains the expected demo users, you can add demonstration content:

```sql
SOURCE C:/Users/1/IdeaProjects/CampusHub/database/demo-data.sql;
```

The project does not include a fixed administrator account. Register a normal user first, then promote it in MySQL:

```sql
UPDATE users
SET role = 'admin'
WHERE username = 'your_username';
```

## Configuration

Default database configuration is stored in:

```text
src/main/resources/database.properties
```

The committed file must not contain real passwords. Use environment variables or JVM system properties for local secrets.

### Environment Variables

| Variable | Description |
| --- | --- |
| `CAMPUSHUB_DB_DRIVER` | JDBC driver, normally `com.mysql.cj.jdbc.Driver` |
| `CAMPUSHUB_DB_URL` | MySQL JDBC URL |
| `CAMPUSHUB_DB_USERNAME` | Database username |
| `CAMPUSHUB_DB_PASSWORD` | Database password |
| `CAMPUSHUB_UPLOAD_DIR` | External directory for uploaded images |
| `CAMPUSHUB_PUBLIC_BASE_URL` | Public or LAN base URL for QR payment pages |

PowerShell example:

```powershell
$env:CAMPUSHUB_DB_URL="jdbc:mysql://localhost:3306/campushub?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai&useSSL=false&allowPublicKeyRetrieval=true"
$env:CAMPUSHUB_DB_USERNAME="root"
$env:CAMPUSHUB_DB_PASSWORD="your_password"
$env:CAMPUSHUB_UPLOAD_DIR="C:\CampusHub\uploads"
```

If `CAMPUSHUB_DB_PASSWORD` is not set and `db.password` is still `PLEASE_SET_ENV`, the application fails fast with a configuration error.

### IntelliJ IDEA Tomcat Configuration

If Tomcat launched by IDEA cannot read system environment variables, add these VM options to the Tomcat run configuration:

```text
-Dcampushub.db.url=jdbc:mysql://localhost:3306/campushub?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai&useSSL=false&allowPublicKeyRetrieval=true
-Dcampushub.db.username=root
-Dcampushub.db.password=your_password
-Dcampushub.upload.dir=C:\CampusHub\uploads
```

## Uploaded Images

Uploaded files are stored outside the deployed WAR or exploded artifact.

Priority:

1. JVM property `campushub.upload.dir`
2. Environment variable `CAMPUSHUB_UPLOAD_DIR`
3. Default directory `%USERPROFILE%\CampusHub\uploads`

The public URL remains:

```text
/uploads/{type}/{filename}
```

`UploadedFileServlet` reads files from the external upload directory. Keep this directory outside Tomcat `webapps` and Maven `target` so uploaded images are not lost after rebuilds or redeployments.

## Build and Test

Run unit tests:

```shell
mvn "-Dmaven.repo.local=target\m2" test
```

Build the WAR package:

```shell
mvn "-Dmaven.repo.local=target\m2" -DskipTests package
```

Generated artifact:

```text
target/CampusHub.war
```

## Deployment

### Formal Handoff Deployment

1. Install JDK 21+, Maven 3.9+, MySQL 8.0+, and Apache Tomcat 9.
2. Create and select the `campushub` database, then restore the exported handoff script:

   ```sql
   CREATE DATABASE IF NOT EXISTS campushub
     DEFAULT CHARACTER SET utf8mb4
     COLLATE utf8mb4_unicode_ci;
   USE campushub;
   SOURCE C:/Users/1/IdeaProjects/CampusHub/src/main/resources/database/campushub.sql;
   ```

3. Configure the runtime database connection. In PowerShell:

   ```powershell
   $env:CAMPUSHUB_DB_URL="jdbc:mysql://localhost:3306/campushub?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai&useSSL=false&allowPublicKeyRetrieval=true"
   $env:CAMPUSHUB_DB_USERNAME="root"
   $env:CAMPUSHUB_DB_PASSWORD="your_password"
   $env:CAMPUSHUB_UPLOAD_DIR="C:\CampusHub\uploads"
   ```

4. Build and verify the project:

   ```shell
   mvn "-Dmaven.repo.local=target\m2" test
   mvn "-Dmaven.repo.local=target\m2" -DskipTests package
   ```

5. Deploy `target/CampusHub.war` to Tomcat 9 `webapps`, or deploy `CampusHub:war exploded` from IntelliJ IDEA.
6. Start Tomcat and open:

   ```text
   http://localhost:8080/CampusHub/
   ```

### IntelliJ IDEA Tomcat Deployment

1. Open the project in IntelliJ IDEA.
2. Configure Project SDK as JDK 21 or later.
3. Add a local Tomcat 9 server in Run/Debug Configurations.
4. Add the `CampusHub:war exploded` artifact in the Deployment tab.
5. Add database and upload settings through environment variables or VM options.
6. Start the Tomcat run configuration.

### WAR Deployment

1. Build `target/CampusHub.war`.
2. Copy it to the Tomcat 9 `webapps` directory.
3. Start Tomcat.
4. Access `/CampusHub/` in the browser.

Common entry points:

| Page | URL |
| --- | --- |
| Home | `/CampusHub/` |
| Login | `/CampusHub/login` |
| Register | `/CampusHub/register` |
| Admin | `/CampusHub/admin` |
| Private Messages | `/CampusHub/private-messages` |

Servlet and filter mapping summary:

![Servlet and filter configuration](docs/figures/figure-5-1-servlet-filter-config.png)

## Local Acceptance Checklist

Before demonstration or grading, run:

```text
docs/acceptance-checklist.md
```

The checklist covers:

- Fresh MySQL initialization.
- Environment variable or VM option configuration.
- Maven test and package verification.
- Tomcat deployment.
- Registration, login, posts, comments, likes, favorites.
- Goods publishing and simulated payment.
- Lost-and-found claims.
- Activity registration.
- Notifications, private messages, search, and administrator moderation.

## Simulated QR Payment

The payment workflow is for coursework demonstration only. It does not call WeChat Pay or transfer real money.

To scan a QR code with a phone:

1. Connect the phone and server to the same LAN.
2. Set `CAMPUSHUB_PUBLIC_BASE_URL` to an address accessible from the phone:

   ```powershell
   $env:CAMPUSHUB_PUBLIC_BASE_URL="http://192.168.1.100:8080"
   ```

3. Restart Tomcat and create a new order QR code.

Do not use `localhost` in QR codes intended for another device.

## Security Notes

- Passwords are hashed with BCrypt.
- Remember Me cookies do not store plaintext passwords.
- SQL uses parameterized `PreparedStatement` queries.
- Filters protect authenticated user routes and administrator routes separately.
- Account cancellation uses soft deletion, anonymization, and related-data cleanup.
- Real database credentials should be provided locally and must not be committed.

## Documentation

- [Database schema specification](database-schema.md)
- [Acceptance checklist](docs/acceptance-checklist.md)
- [Source file reference](CODE_FILES.md)

## Limitations

CampusHub is a Java Web coursework project. Simulated payments, single-node sessions, and local upload storage are suitable for classroom demonstration but are not production-grade replacements for real payment, distributed session management, or object storage.
