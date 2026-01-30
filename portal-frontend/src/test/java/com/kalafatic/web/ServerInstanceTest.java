package com.kalafatic.web;

import com.kalafatic.web.plugins.server.Server;
import com.kalafatic.web.plugins.ServerFactory;
import com.kalafatic.web.plugins.server.ServerType;
import org.junit.Test;
import static org.junit.Assert.*;

public class ServerInstanceTest {

    @Test
    public void testTomcatInstance() {
        Server tomcat = ServerFactory.createServer(ServerType.TOMCAT);
        assertNotNull(tomcat);
        assertEquals("Apache Tomcat", tomcat.getName());
        tomcat.start();
        tomcat.stop();
    }
}
