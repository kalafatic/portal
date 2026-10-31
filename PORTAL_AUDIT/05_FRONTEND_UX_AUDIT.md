# 05. Frontend Architecture & User Experience (UX) Audit

## 1. Frontend Architecture & Technology Stack

The presentation layer is located entirely within `portal-frontend`:
- **Core Technology**: JavaServer Faces 2.2 (Mojarra) with Facelets templating.
- **Component Library**: PrimeFaces 11.0.0.
- **Dependency Injection**: CDI 1.1 (`@Named`, `@Inject`, `@RequestScoped`, `@SessionScoped`, `@ApplicationScoped`).
- **Styling**: Vanilla CSS modular architecture (`style.css`, `menu.css`, `sidebar.css`), PrimeIcons (`pi pi-*`), and PrimeFaces Grid CSS (`ui-g`, `ui-g-12`, `ui-md-6`, etc.).

---

## 2. Facelets Templating & Component Structure

### 2.1 Master Layout (`layout.xhtml`)
- **Structure**:
  - Global `p:growl` with `showDetail="true"` and `life="3000"`.
  - Top navigation bar included via `<ui:include src="/menu.xhtml" />`.
  - Dynamic `styleClass` on `h:body`: `has-sidebar` added conditionally when `#{sessionScope.username != null}`.
  - Standardized `.container` with 100px top padding to accommodate the 60px fixed navigation bar.
  - `.main-content` wrapper with distinct `4px solid #005fb8` blue accent top border.
  - Static enterprise footer.

### 2.2 Navigation & Menu (`menu.xhtml`)
- **Top Navigation Bar**: Standard 60px height, flexbox layout, bold branding (`PORTAL | DASHBOARD`).
- **Dynamic Links**: Active state highlighted using `styleClass="#{view.viewId.contains('...') ? 'nav-active' : ''}"`.
- **Integrated Sidebar**: Renders when user is authenticated (`#{sessionScope.username != null}`).
  - External link to legacy portal (`http://kalafatic.com/portal-v1.html`).
  - Dynamic user project links iterated via `<c:forEach items="#{sidebarBean.projects}" var="project">`.

---

## 3. Managed Beans & Scoping Evaluation

| Bean Class | Scope | Purpose | Scope Evaluation & Findings |
|---|---|---|---|
| `LoginBean` | `@RequestScoped` | Handles login/logout actions | **Correct**. Stateless request scope avoids holding credential data across requests. |
| `ResetPasswordBean` | `@RequestScoped` | Handles password reset form | **Correct**. Transient request-driven state. |
| `ProfileBean` | `@SessionScoped` | Manages authenticated user details | **Acceptable with caution**. Holds `User` entity in session. If user details are modified outside this session or in another tab, data can become stale. |
| `SidebarBean` | `@SessionScoped` | Holds user projects and search filter | **Problematic**. Caches `List<Project>` in session. If another session modifies projects or multiple browser tabs are used, cached projects may be out of sync. |
| `SystemBean` | `@ApplicationScoped` | Exposes JVM / OS info | **Correct**. Immutable/stateless platform metadata. |
| `DevelopBean` | `@RequestScoped` | Returns developer links & metadata | **Correct**. Lightweight request object. |

---

## 4. User Experience (UX) & Visual Evaluation

### 4.1 Visual Design System ("Azure Simplex")
- **Palette**: Clean white (`#ffffff`), subtle borders (`#cccccc`, `#dddddd`), dark text (`#000000`, `#333333`), and Azure Blue (`#005fb8`) for buttons, links, and accents.
- **Typography**: Sharp, sans-serif typography (13px navigation font, 600 weight, 0.5px letter spacing).
- **Component Geometry**: Sharp 0px border-radii (`border-radius: 0px !important;`) applied globally across all PrimeFaces buttons, input fields, cards, and dialogs.

### 4.2 Interactive Patterns & AJAX Interactions
- **Project Filtering**: Implemented via PrimeFaces `<p:inputText id="search">` with `<p:ajax event="keyup" update="projectCards" />`. Works smoothly with instantaneous card filtering.
- **Project Addition**: `<p:dialog widgetVar="addProjectDialog">` with AJAX validation (`oncomplete="if (!args.validationFailed) PF('addProjectDialog').hide();"`).
- **Project Deletion**: Uses `<p:confirmDialog global="true">` for explicit user confirmation before executing deletion, preventing accidental data loss.

### 4.3 Identified Frontend & UX Flaws
1. **Unsecured View Rendering (Security UX Flaw)**:
   In `profile.xhtml`, `develop.xhtml`, and `index.xhtml`, unauthenticated access displays an "Access Denied" or "Please log in" panel embedded inside the page with HTTP 200 rather than issuing a standard HTTP 302 redirect to `login.xhtml`.
2. **Missing Form Validation Constraints**:
   - `Project URL` in `addProjectDialog` is not validated for URL format (accepts arbitrary text or script injection).
   - `Email Address` in `profile.xhtml` lacks an email validator (`<f:validateRegex>` or Bean Validation `@Email`).
3. **Sidebar Search Limitation**:
   The sidebar in `menu.xhtml` directly iterates `sidebarBean.projects` which reflects the filtered search list if a user typed in the dashboard search bar. This couples sidebar rendering to dashboard search query state.
4. **Hardcoded Strings & Incomplete Localization**:
   While `portal-common` provides `messages_en.properties` and `messages_cs.properties`, the XHTML views use hardcoded English strings throughout (`Secure Authentication`, `Add New Project`, `Developer Console`).
