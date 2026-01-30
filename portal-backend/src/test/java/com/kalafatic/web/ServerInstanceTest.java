package com.kalafatic.web;

import com.kalafatic.web.plugins.server.Server;
import com.kalafatic.web.plugins.ServerFactory;
import com.kalafatic.web.plugins.server.ServerType;
import org.junit.Test;
import static org.junit.Assert.*;

public class ServerInstanceTest {

    @Test
    public void testJBossInstance() {
        Server jboss = ServerFactory.createServer(ServerType.JBOSS, 9990);
        assertNotNull(jboss);
        assertEquals("JBoss EAP / WildFly", jboss.getName());
        assertEquals(9990, jboss.getPort());
        jboss.start();
        jboss.stop();
    }

    @Test
    public void testApacheInstance() {
        Server apache = ServerFactory.createServer(ServerType.APACHE, 8081);
        assertNotNull(apache);
        assertEquals("Apache HTTP Server", apache.getName());
        assertEquals(8081, apache.getPort());
        apache.start();
        apache.stop();
    }
}
