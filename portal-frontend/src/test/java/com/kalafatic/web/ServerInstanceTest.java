package com.kalafatic.web;

import com.kalafatic.web.plugins.server.Server;
import com.kalafatic.web.plugins.ServerFactory;
import com.kalafatic.web.plugins.server.ServerType;
import org.junit.Test;
import static org.junit.Assert.*;

public class ServerInstanceTest {

    @Test
    public void testTomcatInstance() {
        Server tomcat = ServerFactory.createServer(ServerType.TOMCAT, 8082);
        assertNotNull(tomcat);
        assertEquals("Apache Tomcat", tomcat.getName());
        assertEquals(8082, tomcat.getPort());
        tomcat.start();
        tomcat.stop();
    }
}
