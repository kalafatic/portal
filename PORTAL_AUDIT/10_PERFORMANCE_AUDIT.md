# 10. Performance, Concurrency & Scalability Audit

## 1. Performance Bottlenecks & Execution Analysis

### 1.1 Database Access Patterns & Query Efficiency
- **Point Queries**:
  - `UserDAO.getUserByUsername`: Query indexed on `username` (`SELECT id, username, name, email FROM users WHERE username=?`). Fast $O(1)$ index lookup assuming `username` is defined as a `UNIQUE` index.
  - `ProjectDAO.getProjectsByUsername`: Query `SELECT name, url FROM projects WHERE username = ?`.
    - **Risk**: If the `projects` table does not have an index on `username`, this query performs a full table scan on every dashboard view and sidebar render.
- **Missing Pagination**:
  - `getProjectsByUsername` returns all rows without a `LIMIT` or pagination clause.
  - While fine for small personal portfolios (10–50 projects), if a user accumulates hundreds of projects, transferring all rows will cause high memory allocations and slow DOM rendering in JSF.

### 1.2 Session Memory & Lifecycle
- `ProfileBean` and `SidebarBean` are `@SessionScoped`.
- `SidebarBean` stores the complete list of projects in session memory:
  ```java
  private List<Project> projects;
  ```
  - When filtering via `getProjects()`:
    ```java
    return projects.stream()
            .filter(p -> p.getName().toLowerCase().contains(searchQuery.toLowerCase().trim()))
            .collect(java.util.stream.Collectors.toList());
    ```
  - Each keypress in the dashboard search bar triggers a filter stream allocation on the server side.
  - Because `projects` is cached indefinitely in the user's HTTP session, memory footprint increases linearly with concurrent active sessions.

---

## 2. Concurrency & Thread Utilization

1. **Connection Pool Contention**:
   - Every operation in `UserDAO` and `ProjectDAO` borrows a database connection from the JNDI pool for the duration of the query.
   - Because queries are straightforward and connections are closed immediately in try-with-resources, connection hold times are minimal.
   - However, if the database becomes slow or unavailable, all worker threads in WildFly/JBoss will block waiting for `ds.getConnection()`, quickly exhausting the Undertow worker thread pool.

2. **Stateless Service Beans**:
   - `UserBean`, `UserDAO`, and `ProjectDAO` are `@ApplicationScoped` and maintain no instance state. They are fully re-entrant and thread-safe.

3. **Frontend Rendering Efficiency**:
   - The Facelets view tree is cached by Mojarra (`javax.faces.FACELETS_REFRESH_PERIOD`).
   - PrimeFaces AJAX partial-page updates (`p:ajax update=":projectCards"`) minimize network payload by re-rendering only the required HTML fragments.
