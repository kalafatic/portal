package com.kalafatic.web.plugins.server;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.logging.Log;
import java.io.File;
import java.io.IOException;

public class ApacheServer implements Server {
    private int port = 80;
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
        info("Starting Apache HTTP Server on port " + port + "...");
    }

    @Override
    public void stop() {
        info("Stopping Apache HTTP Server...");
    }

    @Override
    public void deploy(File artifact, File deployDir) throws IOException {
        info("Deploying " + artifact.getName() + " to Apache at " + deployDir.getAbsolutePath());
        if (!deployDir.exists()) {
            deployDir.mkdirs();
        }
        File destFile = new File(deployDir, artifact.getName());
        FileUtils.copyFile(artifact, destFile);
    }

    @Override
    public String getName() {
        return "Apache HTTP Server";
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
