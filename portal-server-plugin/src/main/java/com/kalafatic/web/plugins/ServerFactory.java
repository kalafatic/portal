package com.kalafatic.web.plugins;

import com.kalafatic.web.plugins.server.Server;
import com.kalafatic.web.plugins.server.ServerType;
import com.kalafatic.web.plugins.server.JBossServer;
import com.kalafatic.web.plugins.server.TomcatServer;
import com.kalafatic.web.plugins.server.ApacheServer;

public class ServerFactory {
    public static Server createServer(ServerType type) {
        switch (type) {
            case JBOSS:
                return new JBossServer();
            case TOMCAT:
                return new TomcatServer();
            case APACHE:
                return new ApacheServer();
            default:
                throw new IllegalArgumentException("Unknown server type: " + type);
        }
    }

    public static Server createServer(ServerType type, int port) {
        Server server = createServer(type);
        server.setPort(port);
        return server;
    }
}
