# 11. Code Quality, Maintainability & Technical Debt Audit

## 1. Code Quality Evaluation

The codebase demonstrates clean formatting, descriptive variable naming, and consistent indentation across Java and XHTML files. However, technical debt exists in the form of code duplication, commented-out logic, and unused configurations.

---

## 2. Technical Debt & Code Smells

### 2.1 Commented-Out Critical Logic (Dead / Broken Code)
- In `portal-common/src/main/java/com/kalafatic/web/UserDAO.java`:
  ```java
  return password.equals(hashedPassword);//BCrypt.checkpw(password, hashedPassword);
  ```
  Leaving commented-out code that alters the security behavior of the system is a severe code smell and security risk.

### 2.2 Duplicated Test Files
- `ServerInstanceTest.java` is duplicated verbatim between `portal-backend` and `portal-frontend`, both testing the build plugin rather than their containing modules.

### 2.3 Hardcoded System Metrics & Strings
- In `portal-frontend/src/main/java/com/kalafatic/web/SystemBean.java`:
  ```java
  public String getServerInfo() {
      return "JBoss EAP 7.4 (WildFly 23)";
  }
  public String getAppVersion() {
      return "2.6.1";
  }
  ```
  And in `DevelopBean.java`:
  ```java
  public String getProjectVersion() {
      return "2.6.1-RELEASE";
  }
  ```
  - Application versions and server info are hardcoded as static string literals across multiple beans instead of being dynamically resolved from the `META-INF/MANIFEST.MF` or Maven build filtering.

### 2.4 Broad Exception Catching
- In both `UserDAO` and `ProjectDAO`, methods catch `Exception` instead of specific `SQLException` or `NamingException`.
  This masks programmer bugs (e.g., `NullPointerException`) as ordinary runtime failures.

### 2.5 Incomplete CDI / Managed Bean Scoping Annotations
- `beans.xml` uses `bean-discovery-mode="all"`, meaning every class in the package is scanned by CDI.
- While `@ApplicationScoped`, `@SessionScoped`, and `@RequestScoped` are used correctly on beans, domain classes (`User`, `Project`) do not specify scopes (they are plain serializable transfer objects), which is appropriate.
