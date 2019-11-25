package com.revolut.revolutaccountmanager;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.servlet.ServletContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Launcher {

    private static Logger LOG = LoggerFactory.getLogger(Launcher.class);

    public static void main(String[] args) {
        Server server = new Server(8081);
        server.setHandler(getServletContextHandler());

        try {
            server.start();
            server.join();
        } catch (Exception ex) {
            LOG.error("Failed to start embedded jetty server.", ex);
        } finally {
            server.destroy();
        }
    }

    private static ServletContextHandler getServletContextHandler() {
        ServletContextHandler contextHandler = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
        contextHandler.setContextPath("/");
        ServletHolder servletHolder = contextHandler.addServlet(ServletContainer.class, "/*");
        servletHolder.setInitOrder(1);
        servletHolder.setInitParameter("jersey.config.server.provider.packages", "com.revolut.revolutaccountmanager.resource");
        return contextHandler;
    }
}
