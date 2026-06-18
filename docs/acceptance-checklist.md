# CampusHub Acceptance Checklist

Use this checklist before classroom demonstration or grading. Start from a fresh MySQL database initialized with `src/main/resources/database/schema.sql`, deploy `target/CampusHub.war` to Tomcat 9, and test with at least two student accounts plus one promoted admin account.

## Environment

| Check | Entry | Account | Expected result | Result |
| --- | --- | --- | --- | --- |
| Initialize database | `SOURCE src/main/resources/database/schema.sql;` | MySQL admin | All tables and seed categories are created without missing-field errors. |  |
| Configure runtime secrets | Environment variables | Local machine | `CAMPUSHUB_DB_URL`, `CAMPUSHUB_DB_USERNAME`, and `CAMPUSHUB_DB_PASSWORD` are set before Tomcat starts. |  |
| Build artifact | `mvn "-Dmaven.repo.local=target\m2" test` | Developer | All unit tests pass. |  |
| Package WAR | `mvn "-Dmaven.repo.local=target\m2" -DskipTests package` | Developer | `target/CampusHub.war` is generated. |  |
| Open home page | `/CampusHub/` | Visitor | Home page loads without server errors. |  |

## Student Workflows

| Check | Entry | Account | Expected result | Result |
| --- | --- | --- | --- | --- |
| Register and log in | `/CampusHub/register`, `/CampusHub/login` | Student A | Registration succeeds, login creates a session, logout returns to visitor state. |  |
| Campus square CRUD | `/CampusHub/` then Square/Post | Student A | Student publishes a post, views detail, comments, likes, favorites, and removes own content. |  |
| Marketplace and mock payment | Market page and goods detail | Student A, Student B | A publishes an online/both item, B creates a mock payment order, payment confirmation marks order paid and goods sold. |  |
| Lost-and-found claim | Lost-and-found page and detail | Student A, Student B | A publishes a found/lost item, B submits a claim, A approves or rejects it, item status updates. |  |
| Activity registration | Activity page and detail | Student A, Student B | A publishes an activity, B registers and cancels, capacity and registration status update correctly. |  |
| Private messaging | `/CampusHub/private-messages` | Student A, Student B | Students can start a conversation, send messages, and unread counts update. |  |
| Notifications | Messages page/header counter | Student A, Student B | Comment, like, favorite, claim, activity, and report-related messages appear and can be marked read. |  |
| Global search | Header search | Student A | Posts, goods, lost-and-found records, activities, and notices appear in search results. |  |

## Admin Workflows

| Check | Entry | Account | Expected result | Result |
| --- | --- | --- | --- | --- |
| Promote admin | SQL update | MySQL admin | `UPDATE users SET role = 'admin' WHERE username = '...';` allows the chosen account to open `/CampusHub/admin`. |  |
| Dashboard | `/CampusHub/admin` | Admin | Platform statistics load without SQL errors. |  |
| User management | Admin user tab | Admin | Admin can disable/enable users and reset passwords for non-canceled accounts. |  |
| Content moderation | Admin content tabs | Admin | Admin can hide posts, goods, lost-and-found records, and activities. |  |
| Notice management | Admin notices tab | Admin | Admin can create, edit, pin, show, and hide notices. |  |
| Report review | Admin reports tab | Admin | Pending reports can be handled or rejected; related content and notifications update. |  |
