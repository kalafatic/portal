# 15. Prioritized Test Implementation Plan

## 1. Test Strategy Overview

This test implementation plan provides concrete, reproducible test specifications to transition PORTAL from its current 15% plugin-only test coverage to a robust, automated test suite covering unit, integration, and security paths.

Tests are designed to run in standard Maven CI/CD builds without requiring an external running JBoss or MySQL instance (using Mockito and H2 in-memory databases).

---

## 2. Prioritized Test Matrix

| Test ID | Target Component | Scenario | Preconditions | Execution Steps | Expected Result | Type | Priority |
|---|---|---|---|---|---|---|---|
| **TEST-SEC-01** | `UserDAO.validate` | Validate BCrypt password authentication | User exists in DB with BCrypt-hashed password `$2a$10$...` | Call `userDAO.validate("testuser", "correctPassword")` | Returns `true` | Unit / Security | **P0** |
| **TEST-SEC-02** | `UserDAO.validate` | Reject invalid password attempt | User exists in DB with BCrypt-hashed password | Call `userDAO.validate("testuser", "wrongPassword")` | Returns `false` | Unit / Security | **P0** |
| **TEST-SEC-03** | `UserDAO.resetPassword` | Password reset hashes password with BCrypt | User exists in DB | Call `userDAO.resetPassword("testuser", "newSecret")` | Returns `true`; DB stores valid BCrypt salt and hash string starting with `$2a$` | Unit / Security | **P0** |
| **TEST-AUTH-01** | `AuthenticationFilter` | Anonymous access to protected URL | No active HTTP session | HTTP GET `/portal/profile.xhtml` | HTTP 302 Redirect to `/portal/login.xhtml` | Integration | **P0** |
| **TEST-AUTH-02** | `AuthenticationFilter` | Authenticated access to protected URL | Session has `username="admin"` | HTTP GET `/portal/profile.xhtml` | HTTP 200 OK; page rendered | Integration | **P0** |
| **TEST-WEB-01** | `LoginBean.login` | Successful login flow | Valid credentials provided | Call `loginBean.login()` with valid username/pwd | Returns `"index?faces-redirect=true"`; session attribute `username` populated | Unit | **P1** |
| **TEST-WEB-02** | `LoginBean.login` | Invalid login flow | Invalid credentials provided | Call `loginBean.login()` with bad pwd | Returns `null`; `FacesMessage` with severity ERROR added to context | Unit | **P1** |
| **TEST-DATA-01** | `ProjectDAO.addProject` | Insert valid user project | User exists; DB connected | Call `projectDAO.addProject("user1", "Proj1", "https://example.com")` | Returns `true`; row inserted into DB | Integration (H2) | **P1** |
| **TEST-DATA-02** | `ProjectDAO.deleteProject` | Delete project by name and username | Project exists in DB | Call `projectDAO.deleteProject("user1", "Proj1")` | Returns `true`; record removed; other users' projects unaffected | Integration (H2) | **P1** |
| **TEST-DATA-03** | `ProjectDAO.getProjects` | Query user projects isolation | 2 users each have 2 projects | Call `projectDAO.getProjectsByUsername("user1")` | Returns List of exactly 2 projects belonging to `user1` | Integration (H2) | **P1** |
| **TEST-UI-01** | `SidebarBean.getProjects` | Search query filters project list | 3 projects in session ("Alpha", "Beta", "Gamma") | Set `searchQuery="alph"`; call `getProjects()` | Returns list containing only "Alpha" (case-insensitive) | Unit | **P2** |
| **TEST-UI-02** | `SidebarBean.getProjects` | Empty search query returns all projects | 3 projects in session | Set `searchQuery=""`; call `getProjects()` | Returns all 3 projects | Unit | **P2** |
| **TEST-USER-01** | `ProfileBean.updateProfile` | Update display name and email | Valid session user loaded | Mutate name/email on `currentUser`; call `updateProfile()` | Returns success `FacesMessage`; DB row updated | Unit | **P2** |
| **TEST-PLUG-01** | `portal-server-plugin` | Full plugin lifecycle in plugin module | Plugin execution | Execute `ServerMojo` with `JBOSS` and verify `.dodeploy` marker generation | Artifact copied; `.dodeploy` created | Unit | **P2** |

---

## 3. Test Automation Implementation Guide

### 3.1 Dependencies to Add in `portal/pom.xml` (`<dependencyManagement>`)
```xml
<dependency>
    <groupId>org.mockito</groupId>
    <artifactId>mockito-core</artifactId>
    <version>4.11.0</version>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <version>2.2.224</version>
    <scope>test</scope>
</dependency>
```

### 3.2 Mocking JNDI Datasource for DAO Unit Tests
Using Mockito and JUnit 4, DAOs can be tested rapidly without a live JBoss container:
```java
@RunWith(MockitoJUnitRunner.class)
public class UserDAOTest {
    @Mock
    private DataSource dataSource;
    @Mock
    private Connection connection;
    @Mock
    private PreparedStatement preparedStatement;
    @Mock
    private ResultSet resultSet;

    @InjectMocks
    private UserDAO userDAO;

    @Test
    public void testValidatePasswordSuccess() throws Exception {
        String rawPassword = "password123";
        String hashed = BCrypt.hashpw(rawPassword, BCrypt.gensalt());

        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getString("password")).thenReturn(hashed);

        assertTrue(userDAO.validate("admin", rawPassword));
    }
}
```
