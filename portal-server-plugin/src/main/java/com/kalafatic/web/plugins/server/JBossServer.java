package com.kalafatic.web.plugins.server;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.logging.Log;
import java.io.File;
import java.io.IOException;

public class JBossServer implements Server {
    private int port = 8080;
    private Log log;

    @Override
    public void setLog(Log log) {
        this.log = log;
    }

    private void info(String message) {
        if (log != null) log.info(message);
        else System.out.println(message);
    }

    @Override
    public void start() {
        info("Starting JBoss Server on port " + port + "...");
    }

    @Override
    public void stop() {
        info("Stopping JBoss Server...");
    }

    @Override
    public void deploy(File artifact, File deployDir) throws IOException {
        info("Deploying " + artifact.getName() + " to JBoss at " + deployDir.getAbsolutePath());
        if (!deployDir.exists()) {
            deployDir.mkdirs();
        }
        File destFile = new File(deployDir, artifact.getName());
        FileUtils.copyFile(artifact, destFile);

        // Create .dodeploy file for JBoss
        File doDeployFile = new File(deployDir, artifact.getName() + ".dodeploy");
        FileUtils.touch(doDeployFile);
        info("Created " + doDeployFile.getName());
    }

    @Override
    public String getName() {
        return "JBoss EAP / WildFly";
    }

    @Override
    public void setPort(int port) {
        this.port = port;
    }

    @Override
    public int getPort() {
        return port;
    }
}
