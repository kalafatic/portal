# 06. Security Architecture & Vulnerability Audit

## 1. Security Overview

The PORTAL application is evaluated against OWASP Top 10 web application vulnerabilities and Java EE security best practices.

Findings are classified into:
- **CRITICAL**: Immediate systemic compromise or authentication failure.
- **HIGH**: Significant authorization bypass or data manipulation.
- **MEDIUM**: Information disclosure or missing defense-in-depth protections.
- **LOW**: Minor configuration deficiencies.
- **INFORMATIONAL**: Security hardening suggestions.

---

## 2. Security Vulnerability Register

### SEC-01: Broken Password Verification Mechanism
- **Severity**: **CRITICAL**
- **Affected File**: `portal-common/src/main/java/com/kalafatic/web/UserDAO.java`
- **Method**: `validate(String username, String password)`
- **Line Reference**: Line 25
- **Vulnerability**:
  ```java
  String hashedPassword = rs.getString("password");
  return password.equals(hashedPassword);//BCrypt.checkpw(password, hashedPassword);
  ```
  The developer commented out `BCrypt.checkpw` and implemented standard Java string equality.
- **Impact**:
  1. If a user's password is stored hashed with BCrypt (as done by `resetPassword()`), the user cannot authenticate because plaintext `password` will never equal `$2a$10$...`.
  2. If passwords in the database are stored in plaintext, the system violates modern cryptographic storage mandates.
- **Remediation**:
  Re-enable `BCrypt.checkpw(password, hashedPassword)`.

---

### SEC-02: Absence of Server-Side Authentication Filter (URL Access Gating)
- **Severity**: **HIGH**
- **Affected Files**: `portal-frontend/src/main/webapp/WEB-INF/web.xml`, `profile.xhtml`, `develop.xhtml`, `index.xhtml`
- **Vulnerability**:
  There are no `<security-constraint>` elements in `web.xml` and no `javax.servlet.Filter` implementation checking session state. Authorization is enforced entirely through JSF view-layer conditional rendering (`rendered="#{sessionScope.username != null}"`).
- **Impact**:
  Unauthenticated clients can request protected pages (`/portal/profile.xhtml`, `/portal/develop.xhtml`). The server compiles the JSF lifecycle and executes bean `@PostConstruct` methods (such as `ProfileBean.init()`). Although the UI hides data, it incurs server resource consumption and exposes partial template markup.
- **Remediation**:
  Implement an `AuthenticationFilter` mapped to `*.xhtml` that intercepts requests, checks `request.getSession(false).getAttribute("username")`, and redirects unauthorized users to `login.xhtml`.

---

### SEC-03: Insecure Direct Object Reference / Missing Authorization in Project Deletion
- **Severity**: **HIGH**
- **Affected Files**: `portal-frontend/src/main/java/com/kalafatic/web/SidebarBean.java`, `portal-common/src/main/java/com/kalafatic/web/ProjectDAO.java`
- **Method**: `SidebarBean.deleteProject(String projectName)`
- **Vulnerability**:
  The deletion action accepts a raw project name string from client-side JSF AJAX parameters (`<f:setPropertyActionListener>` or method argument). If two users have projects with the same name, or if parameters are tampered with, authorization depends entirely on the session username passed to `deleteProject(username, projectName)`. While `username` is pulled from the session, projects lack unique primary key identifiers (`id`).
- **Impact**:
  A user with multiple projects having identical names will inadvertently delete all matching projects in a single click. Furthermore, project creation and deletion do not validate project ownership at a domain service boundary.
- **Remediation**:
  Assign auto-increment primary keys (`id`) to projects and delete by `id` with an explicit ownership verification query: `DELETE FROM projects WHERE id = ? AND username = ?`.

---

### SEC-04: Cross-Site Scripting (XSS) via Unsanitized External Project URLs
- **Severity**: **MEDIUM**
- **Affected Files**: `portal-frontend/src/main/webapp/index.xhtml`, `menu.xhtml`
- **Vulnerability**:
  Project URLs entered via `addProject` are rendered directly in `<h:outputLink value="#{project.url}">`.
  If an attacker stores a malicious payload such as `javascript:alert(document.cookie)` or `data:text/html,...` into `url`, clicking the project card or sidebar link triggers arbitrary script execution in the context of the user's browser.
- **Remediation**:
  Add server-side protocol validation in `SidebarBean` or `ProjectDAO` requiring URLs to start with `http://` or `https://`.

---

### SEC-05: Missing Session Cookie Security Configuration
- **Severity**: **MEDIUM**
- **Affected File**: `portal-frontend/src/main/webapp/WEB-INF/web.xml`
- **Vulnerability**:
  `web.xml` does not declare `<session-config>` with `<cookie-config>`.
- **Impact**:
  `JSESSIONID` cookies may be issued without the `HttpOnly` and `Secure` flags, exposing the session token to client-side script theft or cleartext network interception if HTTPS is not forced.
- **Remediation**:
  Add to `web.xml`:
  ```xml
  <session-config>
      <cookie-config>
          <http-only>true</http-only>
          <secure>true</secure>
      </cookie-config>
      <tracking-mode>COOKIE</tracking-mode>
  </session-config>
  ```

---

### SEC-06: Hardcoded Developer Infrastructure & Credentials Pattern
- **Severity**: **LOW**
- **Affected Files**: `portal/pom.xml`, `portal-backend/datasource-config.cli`
- **Vulnerability**:
  `portal/pom.xml` hardcodes developer usernames and paths (`/home/petr/...`). `datasource-config.cli` references environment variables `${env.DB_USER}` and `${env.DB_PASSWORD}`, which is good practice, but local deployment scripts lack validation.
- **Remediation**:
  Remove personal filesystem paths from shared POMs and externalize deployment directories.
