# 12. Build, Deployment & Operational Audit

## 1. Build Process & Reproducibility

### 1.1 Maven Build Lifecycle
- The build is orchestrated from the root POM via:
  ```bash
  cd portal && mvn clean install
  ```
- All modules compile under Java 1.8.
- Compilation succeeds cleanly without errors or broken dependencies.

### 1.2 Deprecation Warnings
During compilation with modern JDKs, Maven emits:
```
[WARNING] source value 8 is obsolete and will be removed in a future release
[WARNING] target value 8 is obsolete and will be removed in a future release
```
The codebase should be migrated to target Java 11 or Java 17 LTS while maintaining JBoss EAP 7.4 compatibility.

---

## 2. Deployment Packaging & Architecture

### 2.1 EAR Packaging (`portal-backend`)
- Assembles an enterprise archive `portal.ear`.
- Integrates `portal-frontend.war` with context root `/portal`.
- Automatically copies third-party dependencies (`mysql-connector-java`, `jbcrypt`) into the EAR `lib/` directory (`<defaultLibBundleDir>lib</defaultLibBundleDir>`).

### 2.2 `portal-server-plugin` Operation
- Implements custom Mojo: `com.kalafatic.web:portal-server-plugin:2.6.1:deploy`.
- Bound to the `install` phase in `portal-backend` and `portal-frontend`.
- Supports three targets: `JBOSS`, `TOMCAT`, `APACHE`.
- Logic:
  1. Inspects `artifact` (`portal.ear` or `portal.war`).
  2. Copies artifact using `FileUtils.copyFile` to `deployPath` (`/tmp/jboss-deploy` or `/tmp/tomcat-deploy`).
  3. For JBoss, creates `.dodeploy` marker file.
- **Operational Assessment**:
  The plugin works as expected for local deployment testing, but in a production CI/CD pipeline, standard containerized workflows (Docker / Podman) or official plugins (`wildfly-maven-plugin`) are preferred.

---

## 3. Operational Risks & Deployment Hazards

1. **Hardcoded User Paths in Root POM**:
   `portal/pom.xml` contains:
   `<jboss.deploy.path.backend>/home/petr/Servers/jboss-eap-7.4_BE/standalone/deployments</jboss.deploy.path.backend>`
   If a developer runs `mvn verify -Peclipse`, the build will fail with a path not found error unless running on the author's local workstation.
2. **Missing Health Check / Readiness Probe**:
   There is no `/health` or `/ping` endpoint for Kubernetes/OpenShift liveness or readiness probes.
3. **No Database Migration Integration**:
   Database schemas must be set up manually before application start. If a migration is omitted, the application starts but fails on user queries.
