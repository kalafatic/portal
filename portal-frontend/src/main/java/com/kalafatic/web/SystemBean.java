package com.kalafatic.web;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

@Named
@ApplicationScoped
public class SystemBean implements Serializable {
    private static final long serialVersionUID = 1L;

    public String getJavaVersion() {
        return System.getProperty("java.version");
    }

    public String getOsName() {
        return System.getProperty("os.name");
    }

    public String getServerInfo() {
        return "JBoss EAP 7.4 (WildFly 23)";
    }

    public String getAppVersion() {
        return "2.6.1";
    }

    public String getServerTime() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    }

    public String getJvmVendor() {
        return System.getProperty("java.vendor");
    }

    public String getArchitecture() {
        return System.getProperty("os.arch");
    }
}
