# 13. Documentation Audit & Technical Documentation Plan

## 1. Evaluation of Existing Documentation

The repository currently contains exactly one documentation file: `README.md`.

### Review of `README.md`
- **Content**:
  Contains a one-sentence project description (`Web portal for interface with world..`) followed by an Eclipse IDE troubleshooting note regarding `UserBean` compilation.
- **Deficiencies**:
  1. No build instructions or prerequisites (JDK version, Maven version).
  2. No server prerequisites (JBoss EAP 7.4 / WildFly setup instructions).
  3. No database setup guide (schema creation, MySQL user permissions, CLI configuration).
  4. No architecture overview or module descriptions.
  5. No testing instructions.
  6. No API documentation.
  7. No security or credential configuration guidelines.

---

## 2. Technical Documentation Improvement Plan

To bring the documentation to enterprise standards, the following technical documentation suite must be developed:

### 2.1 Architecture & Developer Guides (`docs/architecture/`)
- `SYSTEM_OVERVIEW.md`: System topology, module hierarchy, technology stack, and runtime classloading.
- `DATA_FLOW.md`: Authentication, session management, and project persistence flow diagrams.
- `CODING_STANDARDS.md`: Conventions for JSF Facelets, PrimeFaces component styling, and CDI scoping.

### 2.2 Operational & Deployment Guides (`docs/operations/`)
- `DEPLOYMENT_GUIDE.md`: Step-by-step instructions for deploying `portal.ear` to JBoss EAP 7.4 / WildFly.
- `DATABASE_SETUP.md`: Execution of MySQL DDL schema and `datasource-config.cli`.
- `CONFIGURATION_REFERENCE.md`: Environment variables (`DB_USER`, `DB_PASSWORD`), JNDI datasources, and container profiles.

### 2.3 User & Administration Guides (`docs/user/`)
- `USER_GUIDE.md`: Account login, project management (adding/deleting/filtering), and profile editing.
- `TROUBLESHOOTING.md`: Common runtime errors (database disconnection, JNDI lookup failure, session expiration).
