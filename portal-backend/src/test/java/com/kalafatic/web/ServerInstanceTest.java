package com.kalafatic.web;

import com.kalafatic.web.plugins.server.Server;
import com.kalafatic.web.plugins.ServerFactory;
import com.kalafatic.web.plugins.server.ServerType;
import org.junit.Test;
import static org.junit.Assert.*;

public class ServerInstanceTest {

    @Test
    public void testJBossInstance() {
        Server jboss = ServerFactory.createServer(ServerType.JBOSS);
        assertNotNull(jboss);
        assertEquals("JBoss EAP / WildFly", jboss.getName());
        jboss.start();
        jboss.stop();
    }

    @Test
    public void testApacheInstance() {
        Server apache = ServerFactory.createServer(ServerType.APACHE);
        assertNotNull(apache);
        assertEquals("Apache HTTP Server", apache.getName());
        apache.start();
        apache.stop();
    }
}
