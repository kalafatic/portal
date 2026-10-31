# 03. Dependency & Build Configuration Analysis

## 1. Maven Project Hierarchy

The project utilizes a standard Maven multi-module structure with inheritance from `portal/pom.xml`:

```
               [portal:pom (2.6.1)] (Root Reactor & Parent)
                                 |
     +---------------------------+---------------------------+
     |                           |                           |
[portal-common:jar]     [portal-server-plugin]      [portal-frontend:war]
     ^                           ^                           |
     |                           +------------- (test scope) +
     +-------------------------------------------------------+
     |                                                       |
     +---------------------------+---------------------------+
                                 |
                       [portal-backend:ear]
```

---

## 2. Dependency Management & Versions

Defined in `portal/pom.xml` under `<dependencyManagement>`:
- `junit:junit`: `4.13.2` (managed, scope `test`)
- `javax:javaee-api`: `8.0` (managed, scope `provided`)
- `org.jboss.resteasy:resteasy-jaxrs`: `3.15.6.Final` (managed, scope `provided`)
- `org.jboss.resteasy:resteasy-jackson2-provider`: `3.15.6.Final` (managed, scope `provided`)
- `mysql:mysql-connector-java`: `8.0.33` (managed, compile scope)

### Version & Scope Discrepancies Across Modules

1. **`javax:javaee-api` and `javax:javaee-web-api` Overrides in `portal-backend`**:
   - `portal/pom.xml` manages `javaee-api` at version `8.0`.
   - `portal-backend/pom.xml` explicitly overrides `javaee-api` to version `7.0` and adds `javaee-web-api` at version `7.0`.
   - **Consequence**: Packaging an EAR targeting Java EE 7 while sub-modules compile against Java EE 8 introduces binary incompatibility risks during JBoss EAP 7.4 classloading.

2. **`jbcrypt` Duplication**:
   - Both `portal-common` and `portal-backend` declare direct dependencies on `org.mindrot:jbcrypt:0.4` without `<dependencyManagement>` in the parent POM.
   - If version updates occur, modules can drift out of sync.

3. **JSF API and Implementation in `portal-frontend`**:
   - Declares `javax.faces:javax.faces-api:2.2` (provided) and `org.glassfish:javax.faces:2.2.20` (provided).
   - This correctly delegates JSF implementation to the JBoss EAP container Mojarra subsystem, preventing the classic classloading duplicate collision.

4. **PrimeFaces Dependency**:
   - Declares `org.primefaces:primefaces:11.0.0` (compile scope).
   - Bundled inside `WEB-INF/lib` of `portal-frontend.war`. Verified compatible with JSF 2.2 runtime.

5. **Server Plugin Circular Test Dependency**:
   - Both `portal-frontend` and `portal-backend` depend on `portal-server-plugin` with scope `test` solely to run duplicate unit tests for the plugin.
   - The plugin should be tested within its own module (`portal-server-plugin/src/test`), eliminating artificial dependencies between EAR/WAR modules and a build plugin.

---

## 3. Plugin Management & Build Configuration

| Plugin | Version | Configured Module | Goal / Purpose | Findings / Risks |
|---|---|---|---|---|
| `maven-compiler-plugin` | `3.8.1` / `3.13.0` | Root / `portal-backend` | Compile Java 1.8 sources | Root configures 3.8.1; `portal-backend` specifies 3.13.0 with `testCompile`. Configuration should be centralized. |
| `maven-ear-plugin` | `3.3.0` | `portal-backend` | Package EAR version 7 | Configured to bundle `portal-frontend` as a web module with contextRoot `/portal`. |
| `maven-war-plugin` | `3.4.0` | `portal-frontend`, `portal-backend` | Package WAR | Correctly packages JSF resources; excludes `WEB-INF/lib/jboss-(?!vfs).*.jar`. |
| `wildfly-maven-plugin` | `4.2.0.Final` | `portal-backend` | Deploy to WildFly | Unconfigured execution; default configuration relies on local WildFly instance. |
| `maven-surefire-plugin` | `3.2.5` | `portal-backend` | Execute JUnit tests | Configured explicitly in `portal-backend` to execute tests during EAR packaging. |
| `portal-server-plugin` | `2.6.1` | `portal-backend`, `portal-frontend` | Custom deploy goal | Bound to `install` phase. Copies artifacts to `/tmp/jboss-deploy` and `/tmp/tomcat-deploy`. |

---

## 4. Environment & Operational Coupling in POMs

In `portal/pom.xml`, the following properties are declared:
```xml
<jboss.deploy.path.backend>/home/petr/Servers/jboss-eap-7.4_BE/standalone/deployments</jboss.deploy.path.backend>
<jboss.deploy.path.frontend>/home/petr/Servers/jboss-eap-7.4_FE/standalone/deployments</jboss.deploy.path.frontend>
```
And in `portal-frontend/pom.xml` profile `eclipse`:
```xml
<outputDirectory>${jboss.deploy.path.frontend}</outputDirectory>
```

**Risk Assessment**:
- Builds using the `eclipse` profile will fail or write to invalid paths on any machine other than the original developer's environment.
- Deployment target directories should be passed via CLI properties (`-Djboss.deploy.path=...`) or external environment variables, never hardcoded with personal user home directories.
