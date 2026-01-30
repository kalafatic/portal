package com.kalafatic.web.plugins;

import com.kalafatic.web.plugins.server.Server;
import com.kalafatic.web.plugins.server.ServerType;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.io.IOException;

@Mojo(name = "deploy", defaultPhase = LifecyclePhase.INSTALL)
public class ServerMojo extends AbstractMojo {

    @Parameter(property = "serverType", required = true)
    private String serverType;

    @Parameter(property = "deployPath", required = true)
    private File deployPath;

    @Parameter(property = "port")
    private Integer port;

    @Parameter(defaultValue = "${project.build.directory}/${project.build.finalName}.${project.packaging}", readonly = true)
    private File artifact;

    public void execute() throws MojoExecutionException {
        try {
            ServerType type = ServerType.valueOf(serverType.toUpperCase());
            Server server = (port != null) ? ServerFactory.createServer(type, port) : ServerFactory.createServer(type);
            server.setLog(getLog());

            getLog().info("Selected server: " + server.getName() + " on port " + server.getPort());

            if (artifact.exists()) {
                server.deploy(artifact, deployPath);
                getLog().info("Deployment successful to " + deployPath.getAbsolutePath());
            } else {
                getLog().warn("Artifact not found: " + artifact.getAbsolutePath());
            }

        } catch (IllegalArgumentException e) {
            throw new MojoExecutionException("Invalid server type: " + serverType, e);
        } catch (IOException e) {
            throw new MojoExecutionException("Deployment failed", e);
        }
    }
}
