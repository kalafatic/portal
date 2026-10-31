# 16. Consolidated Risk Register

## 1. Risk Assessment Matrix

Risks are categorized across four quadrants:
- **Security (SEC)**
- **Architecture & Modularity (ARCH)**
- **Operational & Deployment (OPS)**
- **Data Integrity & Persistence (DATA)**

Severity and Likelihood ratings:
- **Severity**: High, Medium, Low
- **Likelihood**: High, Medium, Low
- **Risk Score**: Calculated as combination of Severity $\times$ Likelihood.

---

## 2. Risk Register

| Risk ID | Category | Description & Vulnerability | Severity | Likelihood | Risk Score | Impact & Potential Consequences | Recommended Mitigation | Status |
|---|---|---|---|---|---|---|---|---|
| **RISK-01** | **SEC** | Broken BCrypt password validation in `UserDAO.validate` | **High** | **High** | **CRITICAL** | Users with hashed passwords locked out permanently; potential fallback to plaintext passwords in DB. | Restore `BCrypt.checkpw` in `UserDAO.java`. | Open |
| **RISK-02** | **SEC** | Missing Servlet AuthenticationFilter / URL access gating | **High** | **High** | **HIGH** | Protected endpoints accessible anonymously over HTTP; partial content/DOM rendered. | Implement `AuthenticationFilter` in `portal-frontend`. | Open |
| **RISK-03** | **SEC** | Project URL XSS injection vulnerability | **Medium** | **Medium** | **MEDIUM** | Stored XSS via `javascript:` links in project cards and sidebar navigation. | Validate URL protocol (`http://`, `https://`) in JSF and DAO layers. | Open |
| **RISK-04** | **DATA** | Missing Project surrogate primary key (`id`) | **Medium** | **High** | **HIGH** | Deletion by name deletes all identical project names for a user at once. | Alter `projects` table to add auto-increment `id`; refactor `Project.java` and `ProjectDAO`. | Open |
| **RISK-05** | **DATA** | Lack of versioned DDL migration scripts | **Medium** | **High** | **HIGH** | Manual database setup errors, schema drift across environments, failed deployments. | Introduce Flyway migrations or versioned SQL schema scripts. | Open |
| **RISK-06** | **ARCH** | Broken/Orphaned `LoginServlet` declaration in `web.xml` | **Medium** | **Low** | **LOW** | Deployment warnings or `ClassNotFoundException` if `/login` servlet URL pattern is invoked. | Remove orphan servlet entry from `portal-backend/src/main/webapp/WEB-INF/web.xml`. | Open |
| **RISK-07** | **ARCH** | Dormant JPA `persistence.xml` and JNDI mismatch | **Low** | **Medium** | **LOW** | Divergence between JDBC DAOs (`java:jboss/datasources/MySqlDS`) and JPA config (`java:/MySqlDS`). | Align JNDI datasource names and remove or implement JPA entities. | Open |
| **RISK-08** | **OPS** | Hardcoded developer filesystem paths in root `pom.xml` | **Medium** | **Medium** | **MEDIUM** | Builds with `-Peclipse` profile fail on non-author workstations and CI runners. | Externalize paths to Maven properties with default local build directory fallbacks. | Open |
| **RISK-09** | **OPS** | Zero automated tests for business, DAO, and web tiers | **High** | **High** | **HIGH** | Regression bugs introduced during refactoring go undetected until production runtime. | Implement JUnit + Mockito + H2 test suite per `15_TEST_IMPLEMENTATION_PLAN.md`. | Open |
| **RISK-10** | **SEC** | Missing `HttpOnly` and `Secure` cookie attributes on session | **Medium** | **Low** | **LOW** | Session hijacking via JavaScript access to `JSESSIONID` cookie if XSS occurs. | Configure `<cookie-config>` in `portal-frontend/src/main/webapp/WEB-INF/web.xml`. | Open |
