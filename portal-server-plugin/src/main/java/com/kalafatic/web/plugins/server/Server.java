package com.kalafatic.web.plugins.server;

import org.apache.maven.plugin.logging.Log;
import java.io.File;
import java.io.IOException;

public interface Server {
    void setLog(Log log);
    void start();
    void stop();
    void deploy(File artifact, File deployDir) throws IOException;
    String getName();
    void setPort(int port);
    int getPort();
}
