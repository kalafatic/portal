package com.kalafatic.web;

import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpSession;

@Named
@SessionScoped
public class ProfileBean implements Serializable {

    private static final long serialVersionUID = 1L;
    private User currentUser;

    @Inject
    private UserBean userBean;

    @PostConstruct
    public void init() {
        loadUser();
    }

    public void loadUser() {
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
        if (session != null) {
            String username = (String) session.getAttribute("username");
            if (username != null) {
                currentUser = userBean.getUserByUsername(username);
            }
        }
    }

    public void updateProfile() {
        if (currentUser != null) {
            boolean success = userBean.updateUser(currentUser);
            if (success) {
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Profile updated successfully."));
            } else {
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Failed to update profile."));
            }
        }
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }
}
