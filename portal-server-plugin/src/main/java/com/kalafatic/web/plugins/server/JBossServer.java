package com.kalafatic.web.plugins.server;

import org.apache.commons.io.FileUtils;
import java.io.File;
import java.io.IOException;

public class JBossServer implements Server {
    @Override
    public void start() {
        System.out.println("Starting JBoss Server...");
    }

    @Override
    public void stop() {
        System.out.println("Stopping JBoss Server...");
    }

    @Override
    public void deploy(File artifact, File deployDir) throws IOException {
        System.out.println("Deploying " + artifact.getName() + " to JBoss at " + deployDir.getAbsolutePath());
        if (!deployDir.exists()) {
            deployDir.mkdirs();
        }
        File destFile = new File(deployDir, artifact.getName());
        FileUtils.copyFile(artifact, destFile);

        // Create .dodeploy file for JBoss
        File doDeployFile = new File(deployDir, artifact.getName() + ".dodeploy");
        FileUtils.touch(doDeployFile);
        System.out.println("Created " + doDeployFile.getName());
    }

    @Override
    public String getName() {
        return "JBoss EAP / WildFly";
    }
}
