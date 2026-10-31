# 08. API Architecture & External Integrations Audit

## 1. REST APIs and Web Service Endpoints

### 1.1 In-Tree Endpoints Inventory
- **Inspection Finding**: There are **zero (0)** JAX-RS or RESTful API endpoints implemented in the codebase.
- **Dependency Discrepancy**:
  `portal/pom.xml` and `portal-backend/pom.xml` both declare:
  - `org.jboss.resteasy:resteasy-jaxrs:3.15.6.Final`
  - `org.jboss.resteasy:resteasy-jackson2-provider:3.15.6.Final`
  Yet no class utilizes `@Path`, `@GET`, `@POST`, `@Produces`, or `@Consumes`.
  In `develop.xhtml`, the architectural diagram states:
  `[API] -> [RESTEasy JAX-RS] -> [EJB Services] -> [MySQL]`
  This diagram describes aspirational / planned architecture rather than actual code.

### 1.2 Legacy Servlet Mapping Finding
- In `portal-backend/src/main/webapp/WEB-INF/web.xml`:
  ```xml
  <servlet>
    <servlet-name>LoginServlet</servlet-name>
    <servlet-class>com.kalafatic.web.LoginServlet</servlet-class>
  </servlet>
  <servlet-mapping>
    <servlet-name>LoginServlet</servlet-name>
    <url-pattern>/login</url-pattern>
  </servlet-mapping>
  ```
- **Evidence**: `LoginServlet.java` does not exist in `portal-backend` or any other module. If a client attempts to access `/portal/login`, the container fails to locate the servlet class or throws `ClassNotFoundException`.

---

## 2. External Service & Outbound Integrations

1. **Legacy Portal Link**:
   - `SidebarBean.getOldPortalUrl()` returns `"http://kalafatic.com/portal-v1.html"`.
   - Rendered in `menu.xhtml` sidebar.
   - **Risk**: Communicates over unencrypted HTTP. If an external attacker compromises or spoofs the domain, users can be phished.

2. **Developer Quick Links (`DevelopBean`)**:
   - External links to Red Hat Documentation, JCP JSR-344, PrimeFaces Showcase, and Maven Central.
   - All links point to reputable third-party HTTPS endpoints.

3. **Remote EJB Configuration (`jboss-ejb-client.properties`)**:
   - `portal-frontend/src/main/resources/jboss-ejb-client.properties` declares:
     `remote.connectionprovider.create.options.org.xnio.Options.SSL_ENABLED=false`
     `remote.connections=default`
     `remote.connection.default.host=be-server`
     `remote.connection.default.port=9080`
   - **Evidence**: No EJB interfaces, `@EJB` annotations, or remote naming lookups are present. This configuration is an abandoned artifact from an earlier multi-server prototype.
