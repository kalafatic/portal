# 02. Complete Module and File Inventory

## 1. Inventory Summary

The repository contains 4 Maven sub-modules, 1 parent POM, 1 root README, and Eclipse project metadata files.

```
Total source and configuration files: 47 (excluding hidden IDE files and build targets)
Total lines of code/config: ~2,565 lines
- Java Source Files: 12
- Java Test Files: 2
- XHTML Web Templates: 7
- CSS Stylesheets: 3
- XML Configuration / POMs: 13
- CLI / Properties / Manifests: 8
```

---

## 2. Granular Inventory by Module

### 2.1 Parent Module (`portal`)
- `pom.xml`: Root multi-module reactor POM defining dependency versions, compiler options (Java 1.8), plugin management, and sub-module declarations (`../portal-backend`, `../portal-frontend`, `../portal-common`, `../portal-server-plugin`).

### 2.2 `portal-common` (JAR Module)
- `pom.xml`: Defines artifact packaging (`jar`), dependency on `javax:javaee-api` (provided), and `org.mindrot:jbcrypt:0.4`.
- `src/main/resources/META-INF/beans.xml`: CDI 1.1 bean discovery configuration with `bean-discovery-mode="all"`.
- `src/main/resources/com/kalafatic/web/bundles/messages_en.properties`: English localization bundle for demonstration titles.
- `src/main/resources/com/kalafatic/web/bundles/messages_cs.properties`: Czech localization bundle for demonstration titles.
- `src/main/java/com/kalafatic/web/Project.java`: POJO representing user projects (`name`, `url`), implements `Serializable`.
- `src/main/java/com/kalafatic/web/User.java`: POJO representing application users (`id`, `username`, `name`, `email`), implements `Serializable`.
- `src/main/java/com/kalafatic/web/UserBean.java`: Application-scoped CDI service providing user and project business operations, delegating to DAOs.
- `src/main/java/com/kalafatic/web/UserDAO.java`: Application-scoped DAO managing database operations for authentication and user profiles via JNDI datasource.
- `src/main/java/com/kalafatic/web/ProjectDAO.java`: Application-scoped DAO managing user project CRUD operations via JNDI datasource.

### 2.3 `portal-frontend` (WAR Module)
- `pom.xml`: Packaging `war`, dependencies on `portal-common`, `primefaces:11.0.0`, `javax.faces-api:2.2`, `javax.faces:2.2.20`, `jstl:1.2`, and `portal-server-plugin` (test).
- `src/main/resources/jboss-ejb-client.properties`: Outdated remote EJB client properties (`remote.connection.default.host=be-server`).
- `src/main/webapp/WEB-INF/web.xml`: Web application deployment descriptor registering `javax.faces.webapp.FacesServlet` mapped to `*.xhtml`.
- `src/main/webapp/WEB-INF/beans.xml`: CDI 1.1 bean discovery configuration with `bean-discovery-mode="all"`.
- `src/main/webapp/WEB-INF/templates/layout.xhtml`: Master Facelets layout template with `<ui:insert name="title">`, `<ui:insert name="content">`, global `p:growl`, top menu include, and breadcrumb indicator.
- `src/main/webapp/menu.xhtml`: Top navigation bar component and collapsible sidebar rendering projects and links.
- `src/main/webapp/index.xhtml`: Main dashboard view displaying project cards, search filter, and "Add New Project" modal dialog.
- `src/main/webapp/login.xhtml`: Authentication view with username/password inputs and error messaging.
- `src/main/webapp/profile.xhtml`: User profile management view displaying account information, edit forms, and security recommendations.
- `src/main/webapp/develop.xhtml`: Developer console displaying system properties, runtime metrics, core dependencies table, and architecture map.
- `src/main/webapp/resetPassword.xhtml`: Security view allowing password reset for a given username.
- `src/main/webapp/css/style.css`: Global application styles, CSS variables, PrimeFaces UI component overrides, and layout rules.
- `src/main/webapp/css/menu.css`: Top navigation menu styling, hover effects, and active tab highlights.
- `src/main/webapp/css/sidebar.css`: Sidebar styling, layout offsets, and project list item styles.
- `src/main/java/com/kalafatic/web/LoginBean.java`: Request-scoped CDI bean managing login and session invalidation.
- `src/main/java/com/kalafatic/web/ProfileBean.java`: Session-scoped CDI bean managing user profile loading and updates.
- `src/main/java/com/kalafatic/web/SidebarBean.java`: Session-scoped CDI bean managing project retrieval, search filtering, project creation, and deletion.
- `src/main/java/com/kalafatic/web/SystemBean.java`: Application-scoped CDI bean exposing JVM and system runtime information.
- `src/main/java/com/kalafatic/web/DevelopBean.java`: Request-scoped CDI bean exposing dependency catalog and documentation hyperlinks.
- `src/test/java/com/kalafatic/web/ServerInstanceTest.java`: JUnit 4 unit test asserting Tomcat server creation via `portal-server-plugin`.

### 2.4 `portal-backend` (EAR Module)
- `pom.xml`: Packaging `ear`, configures `maven-ear-plugin` (EAR version 7, context root `/portal`), `wildfly-maven-plugin`, and `portal-server-plugin`.
- `datasource-config.cli`: JBoss CLI script for configuring MySQL JDBC driver module and JNDI datasource `java:jboss/datasources/MySqlDS`.
- `src/main/resources/META-INF/persistence.xml`: JPA 2.1 descriptor defining `portalPU` with Hibernate dialect and `java:/MySqlDS`.
- `src/main/webapp/WEB-INF/web.xml`: Legacy/orphaned descriptor registering non-existent `LoginServlet`.
- `src/main/webapp/WEB-INF/jboss-web.xml`: JBoss Web descriptor declaring context-root `portal`.
- `src/main/webapp/META-INF/jboss-web.xml`: JBoss Web descriptor declaring resource reference for `jdbc/MySqlDS`.
- `ejbModule/META-INF/MANIFEST.MF`: Basic manifest descriptor.
- `src/test/java/com/kalafatic/web/ServerInstanceTest.java`: JUnit 4 test asserting JBoss and Apache server creation via `portal-server-plugin`.

### 2.5 `portal-server-plugin` (Maven Plugin Module)
- `pom.xml`: Packaging `maven-plugin`, dependencies on `maven-plugin-api`, `maven-core`, `commons-io:2.11.0`, and `junit:4.13.2`.
- `src/main/java/com/kalafatic/web/plugins/ServerMojo.java`: Maven Mojo implementing `deploy` goal for deployment automation.
- `src/main/java/com/kalafatic/web/plugins/ServerFactory.java`: Factory creating `Server` instances according to `ServerType`.
- `src/main/java/com/kalafatic/web/plugins/server/ServerType.java`: Enum defining `JBOSS`, `TOMCAT`, and `APACHE`.
- `src/main/java/com/kalafatic/web/plugins/server/Server.java`: Interface defining `start()`, `stop()`, and `deploy()`.
- `src/main/java/com/kalafatic/web/plugins/server/JBossServer.java`: Implementation copying artifacts and touching `.dodeploy` markers.
- `src/main/java/com/kalafatic/web/plugins/server/TomcatServer.java`: Implementation copying artifacts to Tomcat deployment directory.
- `src/main/java/com/kalafatic/web/plugins/server/ApacheServer.java`: Implementation copying artifacts to Apache HTTP directory.

---

## 3. Orphaned, Duplicated, or Obsolete Components

| File / Component | Category | Evidence & Technical Assessment |
|---|---|---|
| `portal-backend/src/main/webapp/WEB-INF/web.xml` | **Obsolete / Broken** | Declares `com.kalafatic.web.LoginServlet`. Class does not exist; EAR module has no web-app build directory. |
| `portal-backend/src/main/resources/META-INF/persistence.xml` | **Dormant / Unused** | Declares `portalPU` with `java:/MySqlDS`, but zero JPA entities exist. |
| `portal-frontend/src/main/resources/jboss-ejb-client.properties` | **Obsolete** | Points to host `be-server` on port `9080`. No remote EJB calls are made. |
| `portal-backend/src/main/webapp/META-INF/jboss-web.xml` & `WEB-INF/jboss-web.xml` | **Redundant / Misplaced** | Located in `portal-backend` instead of `portal-frontend.war`. |
| `ServerInstanceTest.java` (in both backend and frontend) | **Duplicated / Misplaced** | Tests the `portal-server-plugin` classes, not the backend or frontend modules. |
