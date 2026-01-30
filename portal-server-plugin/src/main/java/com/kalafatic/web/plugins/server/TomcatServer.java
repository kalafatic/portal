package com.kalafatic.web.plugins.server;

import org.apache.commons.io.FileUtils;
import java.io.File;
import java.io.IOException;

public class TomcatServer implements Server {
    @Override
    public void start() {
        System.out.println("Starting Tomcat Server...");
    }

    @Override
    public void stop() {
        System.out.println("Stopping Tomcat Server...");
    }

    @Override
    public void deploy(File artifact, File deployDir) throws IOException {
        System.out.println("Deploying " + artifact.getName() + " to Tomcat at " + deployDir.getAbsolutePath());
        if (!deployDir.exists()) {
            deployDir.mkdirs();
        }
        File destFile = new File(deployDir, artifact.getName());
        FileUtils.copyFile(artifact, destFile);
    }

    @Override
    public String getName() {
        return "Apache Tomcat";
    }
}
