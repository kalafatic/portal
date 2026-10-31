# 01. Current Architecture Reconstruction

## 1. System Topology & Architectural Style

The PORTAL system is structured as a **Multi-Tier Java Enterprise (Java EE 8) Application** designed to execute on Red Hat JBoss Enterprise Application Platform 7.4 (or WildFly 23+) backed by an external MySQL 8.0 relational database.

### Architectural Diagram
```
+-----------------------------------------------------------------------------------+
|                                  CLIENT TIER                                      |
|  Web Browser (Desktop / Mobile)                                                   |
+-----------------------------------------------------------------------------------+
                                        |  HTTP / HTTPS (Port 8080/8443)
                                        v
+-----------------------------------------------------------------------------------+
|                        PRESENTATION TIER (portal-frontend.war)                    |
|  - JSF 2.2 Servlet (FacesServlet mapping *.xhtml)                                 |
|  - Master Facelets Template: /WEB-INF/templates/layout.xhtml                      |
|  - View Views: index.xhtml, login.xhtml, profile.xhtml, develop.xhtml, ...        |
|  - CDI Backing Beans (@Named, @RequestScoped, @SessionScoped, @ApplicationScoped) |
|    * LoginBean, ProfileBean, SidebarBean, DevelopBean, SystemBean                 |
+-----------------------------------------------------------------------------------+
                                        |
                   Local CDI Dependency Injection (@Inject)
                                        v
+-----------------------------------------------------------------------------------+
|                           BUSINESS TIER (portal-common.jar)                       |
|  - Service Layer: UserBean (@ApplicationScoped)                                   |
|  - Data Access Objects: UserDAO, ProjectDAO (@ApplicationScoped)                  |
|  - Models / Transfer Objects: User, Project (Serializable)                        |
|  - Resource Bundles: messages_en.properties, messages_cs.properties               |
+-----------------------------------------------------------------------------------+
                                        |
                 JNDI Datasource: java:jboss/datasources/MySqlDS
                                        v
+-----------------------------------------------------------------------------------+
|                           DATABASE TIER (MySQL 8.0.x)                             |
|  - Schema: portal (tables: users, projects)                                       |
+-----------------------------------------------------------------------------------+

+-----------------------------------------------------------------------------------+
|                        PACKAGING & DEPLOYMENT UTILITIES                           |
|  - portal-backend.ear: Packages portal-frontend.war and portal-common.jar         |
|  - portal-server-plugin: Custom Maven Mojo (deploy goal) for local artifact copy  |
+-----------------------------------------------------------------------------------+
```

---

## 2. Module Organization & Responsibilities

The codebase consists of four sub-modules governed by a root Maven reactor `portal/pom.xml`:

| Module | Artifact Type | Responsibilities & Observed Roles |
|---|---|---|
| `portal-common` | `jar` | Core business model (`User`, `Project`), Data Access Objects (`UserDAO`, `ProjectDAO`), business facade (`UserBean`), and i18n message bundles. |
| `portal-frontend` | `war` | JSF 2.2 presentation layer, Facelets templates, PrimeFaces 11 UI components, modular stylesheets (`style.css`, `menu.css`, `sidebar.css`), and CDI backing beans (`LoginBean`, `ProfileBean`, `SidebarBean`, `SystemBean`, `DevelopBean`). |
| `portal-backend` | `ear` | Enterprise Application Archive packaging module that bundles `portal-frontend.war` under context root `/portal`. Also contains dormant deployment artifacts (`jboss-web.xml`, `persistence.xml`, `web.xml`). |
| `portal-server-plugin`| `maven-plugin`| Custom build and deployment tool implementing the Maven `deploy` goal for Apache, Tomcat, and JBoss server directories. |

---

## 3. Layer Separation and Dependency Direction

1. **Compilation Dependencies**:
   - `portal-frontend` depends directly on `portal-common`.
   - `portal-backend` depends on `portal-frontend` (type `war`), `portal-common` (type `jar`), `portal-server-plugin` (scope `test`), `jbcrypt`, and `mysql-connector-java`.
   - `portal-common` has no internal project dependencies (only `javax:javaee-api:provided` and `jbcrypt`).
   - `portal-server-plugin` has no project dependencies (only `maven-plugin-api`, `commons-io`, and `junit`).

2. **Architectural Deviation (Layer Bleed)**:
   - In a pure 3-tier enterprise architecture, presentation WARs communicate with backend EJB or REST tiers across formal interfaces. Here, `portal-frontend` directly invokes `portal-common` DAO classes via CDI in the same classloader.
   - The `portal-backend` module does not contain any Java source code except for a test class `ServerInstanceTest.java`. It serves strictly as an EAR packager.

---

## 4. Execution Flows

### 4.1 Authentication Flow
1. User enters username and password in `login.xhtml`.
2. PrimeFaces `p:commandButton` triggers `#{loginBean.login}` via HTTP POST.
3. `LoginBean` invokes `userBean.validate(username, password)`.
4. `UserBean` delegates to `userDAO.validate(username, password)`.
5. `UserDAO` queries MySQL `SELECT password FROM users WHERE username = ?` via `java:jboss/datasources/MySqlDS`.
6. Result comparison occurs:
   - **Critical Anomaly**: Code executes `return password.equals(hashedPassword);` (plaintext string equality) rather than invoking `BCrypt.checkpw`.
7. If true, `LoginBean` sets `session.setAttribute("username", username)` and redirects to `index.xhtml?faces-redirect=true`.
8. If false, `FacesMessage` (SEVERITY_ERROR) is added to `FacesContext` and displayed via `<p:messages>`.

### 4.2 Project Management Flow
1. User requests `index.xhtml`.
2. JSF renders project grid using `#{sidebarBean.projects}`.
3. `SidebarBean` checks if `projects` collection is null; if null, invokes `refreshProjects()`.
4. `refreshProjects()` reads `username` from `HttpSession` and calls `userBean.getProjectsByUsername(username)`.
5. `ProjectDAO` queries `SELECT name, url FROM projects WHERE username = ?`.
6. Add Project Dialog opens via client widget `PF('addProjectDialog').show()`.
7. Form submission calls `#{sidebarBean.addProject}`:
   - Reads `session.getAttribute("username")`.
   - Delegates to `userBean.addProject(username, name, url)`.
   - `ProjectDAO` executes `INSERT INTO projects (username, name, url) VALUES (?, ?, ?)`.
   - On success, `refreshProjects()` re-queries database, resets inputs, and triggers `<p:growl>`.

### 4.3 Profile Management Flow
1. User navigates to `profile.xhtml`.
2. `@PostConstruct` in `ProfileBean` calls `loadUser()`.
3. Reads username from session and queries `userBean.getUserByUsername(username)`.
4. User updates `name` or `email` and clicks "Save Changes" (`#{profileBean.updateProfile}`).
5. `UserDAO` executes `UPDATE users SET name=?, email=? WHERE username=?`.
6. Notification is displayed via `<p:growl>`.

---

## 5. Architectural Inconsistencies & Anomalies

1. **Dormant Persistence Unit (`portalPU`)**:
   `portal-backend/src/main/resources/META-INF/persistence.xml` defines a JTA persistence unit named `portalPU` with `hibernate.hbm2ddl.auto=update` and datasource `java:/MySqlDS`. However, no `@Entity` classes exist, no `EntityManager` is injected, and the DAOs use raw JDBC with a different datasource name (`java:jboss/datasources/MySqlDS`).
2. **Phantom `LoginServlet`**:
   `portal-backend/src/main/webapp/WEB-INF/web.xml` configures `com.kalafatic.web.LoginServlet` mapped to `/login`. No such class exists. The actual login is handled by `login.xhtml` and `LoginBean.java` in `portal-frontend`.
3. **Hardcoded User Filesystem Paths**:
   `portal/pom.xml` contains hardcoded deployment paths for a local user `/home/petr/Servers/jboss-eap-7.4_BE/...`.
4. **Missing Container Security Boundaries**:
   Access control is enforced only via JSF component rendering (`rendered="#{sessionScope.username != null}"`) rather than container security constraints or servlet filters.
