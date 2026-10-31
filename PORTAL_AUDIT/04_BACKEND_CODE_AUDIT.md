# 04. Backend Code & Architecture Audit

## 1. Backend Overview & Architecture

The backend business and persistence logic resides primarily in `portal-common` with support from `portal-backend`.

Key classes:
- `com.kalafatic.web.UserBean` (Business Facade / Service)
- `com.kalafatic.web.UserDAO` (Data Access Object for Users)
- `com.kalafatic.web.ProjectDAO` (Data Access Object for Projects)
- `com.kalafatic.web.User` (Domain Entity / Model)
- `com.kalafatic.web.Project` (Domain Entity / Model)

---

## 2. In-Depth Component Inspection

### 2.1 `UserDAO.java`
- **Class Scope**: `@ApplicationScoped` CDI bean.
- **Resource Injection**: `@Resource(lookup = "java:jboss/datasources/MySqlDS") private DataSource ds;`.
- **Resource Lifecycle**: Correctly uses Java 7 try-with-resources for `Connection` and `PreparedStatement` in all methods (`validate`, `resetPassword`, `getUserByUsername`, `updateUser`).
- **Defects Identified**:
  1. **Password Validation Logic (CRITICAL)**:
     ```java
     // UserDAO.java, line 25:
     return password.equals(hashedPassword);//BCrypt.checkpw(password, hashedPassword);
     ```
     Plaintext equality check is executed instead of invoking `BCrypt.checkpw`. If a password is reset via `resetPassword` (which stores a BCrypt hashed string), subsequent validation attempts will compare plaintext input to the hash string and fail.
  2. **Unchecked ResultSet Handling in `validate`**:
     Does not close `ResultSet` inside a try-with-resources statement, relying on statement/connection closure.
  3. **Silent Exception Swallowing**:
     ```java
     catch (Exception e) {
         LOGGER.log(Level.SEVERE, "Error validating user: " + username, e);
         return false;
     }
     ```
     While logged, errors (e.g. database disconnect, SQL syntax error, missing table) return `false` or `null`, masking infrastructural outages as authentication failures or empty data.

### 2.2 `ProjectDAO.java`
- **Class Scope**: `@ApplicationScoped` CDI bean.
- **Resource Lifecycle**: Uses nested try-with-resources for `Connection`, `PreparedStatement`, and `ResultSet`.
- **Defects Identified**:
  1. **Missing Data Sanitization / Duplicate Constraint Handling**:
     `addProject` executes `INSERT INTO projects (username, name, url) VALUES (?, ?, ?)`. If duplicate projects or null parameters are supplied, an uncaught SQL constraint violation occurs, caught broadly and returning `false`.
  2. **Deletion Scope**:
     `deleteProject(String username, String name)` uses `DELETE FROM projects WHERE username = ? AND name = ?`. If multiple projects exist with the exact same name for the same user, all of them are deleted simultaneously without warning.
  3. **Empty Collection on Error**:
     `getProjectsByUsername` returns an empty `ArrayList` if a database exception occurs. The caller cannot distinguish between "User has 0 projects" and "Database connection timed out".

### 2.3 `UserBean.java`
- **Class Scope**: `@ApplicationScoped` CDI bean.
- **Responsibilities**: Acts as a thin delegation facade injecting `UserDAO` and `ProjectDAO`.
- **Defects Identified**:
  1. **Redundant Facade Layer**:
     The class provides no added business rules, caching, transaction orchestration, or input validation. It simply mirrors the DAO signatures.
  2. **Localization Method Issue (`getString`)**:
     ```java
     public String getString(String key, String lang) {
         Locale locale = new Locale(lang);
         ResourceBundle bundle = ResourceBundle.getBundle("com.kalafatic.web.bundles.messages", locale);
         return bundle.getString(key);
     }
     ```
     Instantiates a new `Locale` and loads `ResourceBundle` on every call. If called frequently, this causes unnecessary garbage collection and bundle resolution overhead.

---

## 3. Transaction Boundaries & Concurrency

1. **Absence of Declarative or Programmatic Transactions**:
   - Neither `UserDAO` nor `ProjectDAO` use `@Transactional` (from `javax.transaction`) or inject `UserTransaction`.
   - Each JDBC operation relies on JDBC auto-commit (`conn.getAutoCommit() == true`).
   - Any multi-step operation (e.g., creating a user and initializing default projects) cannot be executed atomically.

2. **Thread Safety & State Management**:
   - Both `UserBean`, `UserDAO`, and `ProjectDAO` are `@ApplicationScoped` and stateless, meaning single instances are shared across all concurrent HTTP requests.
   - Because no mutable instance variables exist, the beans are thread-safe.
   - However, connection borrowing from the JNDI `DataSource` depends on underlying HikariCP/IronJacamar connection pool sizing in JBoss.

3. **Exception Handling Strategy**:
   - Broad `catch (Exception e)` blocks catch both checked `SQLException` and unchecked runtime exceptions (`NullPointerException`, `IllegalStateException`).
   - Error messages are logged via `java.util.logging.Logger` at level `SEVERE`, but no custom domain exceptions (e.g., `UserNotFoundException`, `DataAccessException`) are thrown to allow the presentation tier to display contextual error information.
