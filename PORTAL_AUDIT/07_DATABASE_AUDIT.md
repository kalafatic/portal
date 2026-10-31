# 07. Database Architecture & Data Integrity Audit

## 1. Database Architecture & Technology

The system connects to an external **MySQL 8.0** database via JNDI data sources in JBoss EAP / WildFly.

### Configuration Analysis
- **Datasource CLI Configuration** (`portal-backend/datasource-config.cli`):
  ```
  data-source add --jndi-name=java:jboss/datasources/MySqlDS \
      --name=MySqlPool \
      --connection-url=jdbc:mysql://localhost:3306/portal \
      --driver-name=mysql \
      --user-name=${env.DB_USER} \
      --password=${env.DB_PASSWORD}
  ```
- **Driver Module**: `mysql-connector-java-8.0.33.jar` with `com.mysql.cj.jdbc.Driver`.

---

## 2. Inferred Database Schema & Entities

Because no DDL files (`.sql`) or Flyway/Liquibase migration scripts exist in the repository, the database schema is reconstructed from JDBC statements across `UserDAO` and `ProjectDAO`:

### 2.1 Table: `users`
Inferred from `UserDAO.java`:
```sql
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    name VARCHAR(255),
    email VARCHAR(255)
);
```

### 2.2 Table: `projects`
Inferred from `ProjectDAO.java`:
```sql
CREATE TABLE IF NOT EXISTS projects (
    id INT AUTO_INCREMENT PRIMARY KEY, -- Note: Project entity in code does NOT have an ID field!
    username VARCHAR(100) NOT NULL,
    name VARCHAR(255) NOT NULL,
    url VARCHAR(1024),
    CONSTRAINT fk_projects_username FOREIGN KEY (username) REFERENCES users(username) ON DELETE CASCADE
);
```

---

## 3. Data Integrity & Consistency Findings

### 3.1 Datasource JNDI Naming Inconsistency
- In `UserDAO.java` and `ProjectDAO.java`:
  `@Resource(lookup = "java:jboss/datasources/MySqlDS")`
- In `portal-backend/src/main/resources/META-INF/persistence.xml`:
  `<jta-data-source>java:/MySqlDS</jta-data-source>`
- In `portal-backend/src/main/webapp/META-INF/jboss-web.xml`:
  `<res-ref-name>jdbc/MySqlDS</res-ref-name>` mapped to `java:jboss/datasources/MySqlDS`
- **Assessment**:
  The application uses three differing JNDI lookup strings. While DAOs align with `datasource-config.cli`, the persistence unit is misconfigured and will fail to bind if JPA is ever activated.

### 3.2 Missing Entity Identity on `Project`
- The domain model `Project.java` only contains `name` and `url`.
- In `ProjectDAO.deleteProject(username, name)`:
  Deletion is performed strictly using the composite condition `WHERE username = ? AND name = ?`.
- **Consequence**:
  There is no surrogate primary key in the Java domain object. If a user adds two projects with identical or empty names, the database may insert both, but deleting one deletes all duplicates simultaneously.

### 3.3 Lack of Foreign Key Constraints & Orphaned Records
- Without explicit migration scripts, if a database is created manually without `FOREIGN KEY (username) REFERENCES users(username)`, deleting a user from `users` leaves orphaned records in `projects`.
- Furthermore, `users.username` updates via `UserDAO.updateUser` only update `name` and `email`, but if username changes were allowed, projects would become disconnected.

### 3.4 Missing Connection Pool Tuning & Connection Leak Analysis
- Both DAOs obtain connections via `ds.getConnection()` inside try-with-resources blocks.
- **Verification**: In `UserDAO` and `ProjectDAO`, `Connection` and `PreparedStatement` are wrapped in try-with-resources, preventing unclosed connection leaks.
- **Risk**: Connection timeouts, maximum pool size, and validation queries (`SELECT 1`) are not specified in `datasource-config.cli`, exposing the server to dead connection exhaustion if MySQL restarts or drops idle TCP connections.
