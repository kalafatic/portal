# 09. Testing Strategy & Test Implementation Audit

## 1. Existing Test Inventory

An audit of all automated tests across the codebase revealed exactly **two (2)** test files containing **three (3)** individual test methods:

| Test Class | Module | Test Methods | Tested Functionality |
|---|---|---|---|
| `com.kalafatic.web.ServerInstanceTest` | `portal-backend` | `testJBossInstance()`, `testApacheInstance()` | Instantiates `ServerFactory.createServer(...)` for JBoss and Apache, checks port and calls `start()` / `stop()`. |
| `com.kalafatic.web.ServerInstanceTest` | `portal-frontend` | `testTomcatInstance()` | Instantiates `ServerFactory.createServer(ServerType.TOMCAT, 8082)`, checks port and calls `start()` / `stop()`. |

---

## 2. Test Quality & Coverage Evaluation

### 2.1 Critical Coverage Gaps
1. **Zero Coverage on Business Logic**:
   - `UserBean`: 0% coverage.
   - `UserDAO`: 0% coverage (including critical authentication, password hashing, and profile update routines).
   - `ProjectDAO`: 0% coverage (including project addition, deletion, and query filtering).
2. **Zero Coverage on Web Presentation / Backing Beans**:
   - `LoginBean`: 0% coverage (login flow, session creation, validation error handling).
   - `ProfileBean`: 0% coverage.
   - `SidebarBean`: 0% coverage (including in-memory search filtering stream logic).
   - `ResetPasswordBean`: 0% coverage.
3. **Zero Integration or Database Tests**:
   - No mock datasource or in-memory database (such as H2) tests exist.
   - SQL queries are untested until deployment against a live MySQL instance.
4. **Zero Security & Boundary Tests**:
   - No tests verifying BCrypt password hash generation or verification.
   - No tests verifying SQL injection resilience or XSS prevention.

### 2.2 Misplaced and Redundant Tests
- The two test files named `ServerInstanceTest` are located in `portal-backend` and `portal-frontend`, but they test classes from `portal-server-plugin` (`ServerFactory`, `JBossServer`, `TomcatServer`, `ApacheServer`).
- These tests do not test the frontend or backend modules at all.
- To execute these tests, `portal-frontend` and `portal-backend` must add `portal-server-plugin` as a Maven test dependency, introducing unnecessary inter-module coupling.
