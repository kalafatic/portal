package com.kalafatic.web;

import java.io.Serializable;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

@ManagedBean
@SessionScoped
public class SidebarBean implements Serializable {

    private static final long serialVersionUID = 1L;
    private List<Project> projects;

    public List<Project> getProjects() {
        if (projects == null) {
            HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
            if (session != null) {
                String username = (String) session.getAttribute("username");
                if (username != null) {
                    projects = UserBean.getProjectsByUsername(username);
                }
            }
        }
        return projects;
    }

    public String getOldPortalUrl() {
        return "http://kalafatic.com/portal-v1.html";
    }
}
