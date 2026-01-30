package com.kalafatic.web.plugins.server;

import java.io.File;
import java.io.IOException;

public interface Server {
    void start();
    void stop();
    void deploy(File artifact, File deployDir) throws IOException;
    String getName();
}
