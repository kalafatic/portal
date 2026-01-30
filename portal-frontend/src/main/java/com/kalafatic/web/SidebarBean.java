package com.kalafatic.web;

import java.io.Serializable;
import java.util.List;

import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpSession;

@Named
@SessionScoped
public class SidebarBean implements Serializable {

    private static final long serialVersionUID = 1L;
    private List<Project> projects;
    private String newProjectName;
    private String newProjectUrl;

    @Inject
    private UserBean userBean;

    public List<Project> getProjects() {
        if (projects == null) {
            refreshProjects();
        }
        return projects;
    }

    public void refreshProjects() {
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
        if (session != null) {
            String username = (String) session.getAttribute("username");
            if (username != null) {
                projects = userBean.getProjectsByUsername(username);
            }
        }
    }

    public void addProject() {
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
        if (session != null) {
            String username = (String) session.getAttribute("username");
            if (username != null && newProjectName != null && !newProjectName.isEmpty()) {
                boolean success = userBean.addProject(username, newProjectName, newProjectUrl);
                if (success) {
                    refreshProjects();
                    newProjectName = null;
                    newProjectUrl = null;
                    FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Project added successfully."));
                } else {
                    FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Failed to add project."));
                }
            }
        }
    }

    public void deleteProject(String projectName) {
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
        if (session != null) {
            String username = (String) session.getAttribute("username");
            if (username != null) {
                boolean success = userBean.deleteProject(username, projectName);
                if (success) {
                    refreshProjects();
                    FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Project deleted successfully."));
                } else {
                    FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Failed to delete project."));
                }
            }
        }
    }

    public String getOldPortalUrl() {
        return "http://kalafatic.com/portal-v1.html";
    }

    // Getters and Setters for new project fields
    public String getNewProjectName() { return newProjectName; }
    public void setNewProjectName(String newProjectName) { this.newProjectName = newProjectName; }

    public String getNewProjectUrl() { return newProjectUrl; }
    public void setNewProjectUrl(String newProjectUrl) { this.newProjectUrl = newProjectUrl; }
}
