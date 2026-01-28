package com.kalafatic.web;

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Named
@RequestScoped
public class DevelopBean implements Serializable {
    private static final long serialVersionUID = 1L;

    public String getProjectVersion() {
        return "2.6.1-RELEASE";
    }

    public List<ResourceLink> getResources() {
        List<ResourceLink> links = new ArrayList<>();
        links.add(new ResourceLink("JBoss EAP Documentation", "https://access.redhat.com/documentation/en-us/red_hat_jboss_enterprise_application_platform/7.4"));
        links.add(new ResourceLink("JSF 2.2 Specification", "https://jcp.org/en/jsr/detail?id=344"));
        links.add(new ResourceLink("PrimeFaces ShowCase", "https://www.primefaces.org/showcase/"));
        links.add(new ResourceLink("Maven Repository", "https://mvnrepository.com/"));
        return links;
    }

    public List<DependencyInfo> getDependencies() {
        List<DependencyInfo> deps = new ArrayList<>();
        deps.add(new DependencyInfo("PrimeFaces", "11.0.0", "UI Components"));
        deps.add(new DependencyInfo("jBCrypt", "0.4", "Security/Hashing"));
        deps.add(new DependencyInfo("Java EE API", "8.0", "Core Platform"));
        deps.add(new DependencyInfo("MySQL Connector", "8.0.33", "Database"));
        return deps;
    }

    public static class ResourceLink {
        private String name;
        private String url;
        public ResourceLink(String name, String url) { this.name = name; this.url = url; }
        public String getName() { return name; }
        public String getUrl() { return url; }
    }

    public static class DependencyInfo {
        private String name;
        private String version;
        private String purpose;
        public DependencyInfo(String name, String version, String purpose) {
            this.name = name; this.version = version; this.purpose = purpose;
        }
        public String getName() { return name; }
        public String getVersion() { return version; }
        public String getPurpose() { return purpose; }
    }
}
