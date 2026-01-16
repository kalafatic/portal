# portal
Web portal for interface with world..

## Troubleshooting

### Eclipse IDE: "Undefined type: com.kalafatic.web.UserBean"

If you see this error in the Eclipse IDE editor for `index.jsp`, but the command-line `mvn clean install` build is successful, it means your Eclipse project metadata is out of sync with the Maven `pom.xml` configuration.

This is a common issue and is easy to fix.

**Solution:**

1.  In the "Project Explorer" in Eclipse, select the `portal-frontend` project (and optionally the other projects as well).
2.  Right-click on the selected project(s).
3.  Go to **Maven -> Update Project...**.
4.  A dialog will appear. Ensure the project(s) are checked and click **OK**.

This will force Eclipse to re-read the `pom.xml` files and correctly configure the project build path, which will resolve the error.
