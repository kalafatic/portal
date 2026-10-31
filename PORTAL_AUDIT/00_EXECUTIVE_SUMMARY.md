# 00. Executive Summary: PORTAL Architecture, Quality, and Evolution Audit

## 1. Audit Overview & Objectives

This audit provides an exhaustive, evidence-based architectural, code quality, and security evaluation of the **PORTAL** application (version `2.6.1`). The system is a multi-module Java enterprise platform structured as a multi-tier Maven project targeting JBoss EAP 7.4 / WildFly application server environments with a MySQL datastore and a JSF 2.2 / PrimeFaces 11.0 presentation layer.

The analysis is based strictly on verified source code, build scripts, deployment descriptors, configuration files, and runtime tests across the four sub-modules:
- `portal-common` (JAR: domain entities, DAO persistence layer, business services)
- `portal-backend` (EAR: enterprise packaging, obsolete/dormant configurations)
- `portal-frontend` (WAR: JSF/Facelets presentation tier, CDI web backing beans, stylesheets)
- `portal-server-plugin` (Maven Plugin: multi-server deployment automation for JBoss, Tomcat, and Apache)

---

## 2. Executive Answers to Core Audit Questions

### 1. What is the actual state of the PORTAL application?
The PORTAL codebase is a compact, transitional Java EE application undergoing modernization from legacy JSP/Servlet structures toward CDI, JSF 2.2 Facelets, and PrimeFaces 11.0 components. While the visual and user interface tier is functional and visually coherent (following a strict professional "Azure Simplex" design system), the backend architecture suffers from major structural contradictions:
- The `portal-backend` module packages an EAR file with persistence units and legacy web descriptors, yet contains no executable backend services or EJBs; the web tier in `portal-frontend` communicates directly with MySQL via `portal-common` DAOs over local JNDI rather than utilizing remote EJBs or REST endpoints.
- The repository contains critical security flaws in credential handling and session enforcement.
- Automated testing is almost non-existent for business and persistence logic (only 3 unit tests verify mock deployment plugin instances).

### 2. What works correctly?
- **Build System**: The root Maven reactor (`portal/pom.xml`) builds cleanly across all four modules using `mvn clean install` / `mvn test` under Java 8 / 11 / 17 environments.
- **Frontend Presentation Layer**: The JSF Facelets templating structure (`layout.xhtml`, `menu.xhtml`) renders cleanly with unified typography, consistent navigation headers, responsive PrimeFaces grid layouts (`.ui-g`), and feedback notifications (`p:growl`).
- **CDI Component Wiring**: Beans in `portal-frontend` (`LoginBean`, `ProfileBean`, `SidebarBean`, `SystemBean`, `DevelopBean`) correctly leverage CDI 1.1+ annotations (`@Named`, `@Inject`, `@SessionScoped`, `@RequestScoped`, `@ApplicationScoped`) with active `beans.xml` descriptors.
- **Plugin Infrastructure**: The custom Maven plugin `portal-server-plugin` implements modular server abstraction (`ServerFactory`, `JBossServer`, `TomcatServer`, `ApacheServer`) for local filesystem artifact copying and `.dodeploy` marker generation.

### 3. What is defective or risky?
- **Defective Credential Verification (CRITICAL)**: `UserDAO.validate(String, String)` bypasses BCrypt hashing completely:
  ```java
  // In UserDAO.java:
  return password.equals(hashedPassword); // BCrypt.checkpw(password, hashedPassword);
  ```
  Meanwhile, `UserDAO.resetPassword(...)` hashes passwords using `BCrypt.hashpw(...)`. As a direct consequence, once a password is reset, `validate()` compares raw plaintext input with a `$2a$` BCrypt hash string, locking out users permanently or failing authentication.
- **Phantom Servlet Descriptor**: `portal-backend/src/main/webapp/WEB-INF/web.xml` declares `com.kalafatic.web.LoginServlet` mapped to `/login`. However, `LoginServlet.java` does not exist anywhere in the codebase.
- **Unsynchronized Session State**: `ProfileBean` and `SidebarBean` load data into `@SessionScoped` state but do not implement automated cache invalidation or re-hydration on profile updates or cross-session changes.
- **Missing Transactions**: Neither `UserDAO` nor `ProjectDAO` use container-managed or manual JTA transactions (`UserTransaction`). Failures in multi-statement workflows will cause orphaned updates.

### 4. What are the most important architectural weaknesses?
- **Architectural Disconnect Between Modules**: The architecture implies a three-tier EAR deployment (`portal-backend.ear` bundling `portal-frontend.war`), but the WAR directly bundles and invokes `portal-common` JDBC DAOs. The persistence unit defined in `portal-backend/src/main/resources/META-INF/persistence.xml` (`portalPU`) is never referenced by any JPA entity or EntityManager; it is completely dormant.
- **JNDI Lookup Inconsistency**: `portal-backend/src/main/resources/META-INF/persistence.xml` specifies `java:/MySqlDS`, whereas `UserDAO` and `ProjectDAO` look up `java:jboss/datasources/MySqlDS`.
- **Client-Side Authorization Enforcement Only**: Protected pages (`profile.xhtml`, `develop.xhtml`, `index.xhtml`) enforce security strictly via JSF `rendered="#{sessionScope.username != null}"` and UI conditional markup rather than an HTTP Filter or security constraint. Any unauthenticated user requesting the URL directly receives rendered HTML shells with HTTP 200 OK.
- **Hardcoded Development Paths in POM**: `portal/pom.xml` hardcodes developer filesystem paths (`/home/petr/Servers/jboss-eap-7.4_BE/...`).

### 5. What security problems exist?
- **Broken Password Validation (Severity: CRITICAL)**: Plaintext string comparison against BCrypt hash strings in `UserDAO.java`.
- **Missing Authentication Filter / Authorization Bypass (Severity: HIGH)**: Unauthenticated requests directly load secure XHTML views without HTTP redirects or 401/403 challenges.
- **Direct Object Reference & IDOR in Project DAO (Severity: MEDIUM-HIGH)**: While `getProjectsByUsername` scopes by session username, operations accept user-provided strings without authorization checks at the service layer.
- **Cross-Site Scripting (XSS) Potential in External Links (Severity: MEDIUM)**: `sidebarBean.projects` render links using raw URLs (`#{project.url}`) without URI protocol validation (e.g. `javascript:` payload injection).
- **Session Hijacking Exposure (Severity: MEDIUM)**: Absence of `web.xml` security constraints (`<http-only>`, `<secure-cookie>`) and CSRF tokens on non-JSF requests.

### 6. What functionality is missing?
- **User Registration and Role Management**: No registration interface or role-based access control (RBAC). All authenticated users are statically labeled "Developer / Administrator".
- **RESTful API Tier**: Although RESTEasy dependencies are present in `portal-backend/pom.xml`, not a single JAX-RS endpoint or resource class is implemented.
- **Database Schema DDL & Migration Scripts**: No SQL migration scripts (Liquibase or Flyway) or initial DDL schema exist in the repository; only a JBoss CLI configuration script (`datasource-config.cli`) is provided.
- **Automated Business Logic Unit & Integration Tests**: 0% test coverage on `UserBean`, `UserDAO`, `ProjectDAO`, `ProfileBean`, and `SidebarBean`.

### 7. What should be improved first?
1. **Fix Authentication Logic**: Immediately restore `BCrypt.checkpw(password, hashedPassword)` in `UserDAO.java`.
2. **Implement an HTTP Authentication Filter**: Add a standard `javax.servlet.Filter` in `portal-frontend` protecting restricted XHTML endpoints (`/index.xhtml`, `/profile.xhtml`, `/develop.xhtml`) and redirecting anonymous requests to `/login.xhtml`.
3. **Harmonize JNDI Datasource Names & Clean Obsolete Artifacts**: Align JNDI datasource references across CLI, persistence descriptors, and DAOs; remove phantom `LoginServlet` declaration.
4. **Automate DDL & Database Migrations**: Introduce Flyway or a versioned `schema.sql` script creating `users` and `projects` tables with foreign keys and constraints.

### 8. What should not be changed?
- **Do NOT rewrite the JSF / PrimeFaces presentation layer**: The UI architecture is clean, responsive, and matches the required professional enterprise design system.
- **Do NOT replace CDI with an external framework (e.g., Spring Boot)**: The current CDI 1.1 / Java EE container architecture is lightweight, standard-compliant, and well-suited for JBoss EAP / WildFly.
- **Do NOT alter the Maven multi-module decomposition**: The division of `portal-common`, `portal-frontend`, and `portal-server-plugin` provides clear compilation boundaries.

### 9. How should the project evolve?
The application should evolve incrementally across four distinct phases:
- **Phase 1: Remediation & Security Hardening**: Fix authentication, introduce security filters, validate input, eliminate phantom configurations.
- **Phase 2: Data Integrity & Persistence Architecture**: Introduce schema versioning, foreign keys, connection pooling validation, and transactional boundaries.
- **Phase 3: Automated Testing & CI/CD**: Introduce Arquillian / Mockito / H2 unit and integration tests; decouple hardcoded deployment paths into Maven profiles or environment variables.
- **Phase 4: API Tier & Extensibility**: Implement real JAX-RS REST endpoints for cross-platform integration and complete role-based authorization.

### 10. What are the concrete next implementation steps?
1. Edit `UserDAO.java` line 25 to replace `.equals` with `BCrypt.checkpw(password, hashedPassword)`.
2. Create `com.kalafatic.web.filter.AuthenticationFilter` in `portal-frontend` and register it in `web.xml` for `*.xhtml`.
3. Remove `LoginServlet` declaration from `portal-backend/src/main/webapp/WEB-INF/web.xml`.
4. Create `db/migration/V1__initial_schema.sql` with normalized `users` and `projects` table definitions.
5. Create unit tests using JUnit 4 and Mockito for `UserBean`, `UserDAO`, and `SidebarBean`.

---

## 3. High-Level System Quality Scorecard

| Quality Dimension | Current Score | Target Score | Primary Gap / Risk |
|---|---|---|---|
| **Architecture & Modularity** | B- (70%) | A (95%) | EAR packaging disconnect; dormant `portalPU` unit; missing service transaction boundaries. |
| **Backend Implementation** | C+ (65%) | A (95%) | Raw JDBC without transactions; unhandled DAO exceptions returning empty collections/false. |
| **Frontend & UX** | A- (88%) | A (95%) | Polished Azure design, clean PrimeFaces usage; missing HTTP-level access gating. |
| **Security & Authentication** | D (55%) | A (95%) | Plaintext password check bug; missing AuthFilter; unvalidated external URL redirects. |
| **Database & Integrity** | C (60%) | A (90%) | No DDL scripts; no DB foreign keys enforced in code; JNDI datasource name inconsistencies. |
| **Test Coverage & Reliability**| F (15%) | A (90%) | Only 3 plugin tests; 0% unit/integration tests for backend and frontend beans. |
| **Build & Operations** | B (75%) | A (95%) | Successful Maven build; hardcoded local user paths in root POM (`/home/petr/...`). |
