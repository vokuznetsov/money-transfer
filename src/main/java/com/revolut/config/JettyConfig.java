package com.revolut.config;

import com.google.inject.Inject;
import com.google.inject.servlet.GuiceFilter;
import com.revolut.Application;
import com.typesafe.config.Config;
import java.net.InetSocketAddress;
import java.net.URISyntaxException;
import java.util.Objects;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;

public class JettyConfig {

    private static final String SWAGGER_UI_VERSION = "3.20.1";

    private final GuiceFilter filter;
    private final Config config;

    private Server server;

    @Inject
    public JettyConfig(GuiceFilter filter, Config config) {
        this.filter = filter;
        this.config = config.getConfig("http");
    }

    public void start() throws Exception {
        InetSocketAddress address = InetSocketAddress.createUnresolved(
                config.getString("host"),
                config.getInt("port"));

        server = new Server(address);

        ContextHandlerCollection contexts = new ContextHandlerCollection();
        contexts.setHandlers(new Handler[]{buildApiContext(), buildSwaggerContext()});
        server.setHandler(contexts);
        server.start();
    }

    public void join() throws InterruptedException {
        server.join();
    }

    public void stop() throws Exception {
        server.stop();
    }

    private ServletContextHandler buildApiContext() {
        final ServletContextHandler context = new ServletContextHandler();
        context.setContextPath("/");
        context.addFilter(new FilterHolder(filter), "/*", null);
        context.addServlet(DefaultServlet.class, "/");
        return context;
    }

    private ContextHandler buildSwaggerContext() throws URISyntaxException {
        ResourceHandler rh = new ResourceHandler();
        rh.setResourceBase(Objects.requireNonNull(Application.class.getClassLoader()
                .getResource("META-INF/resources/webjars/swagger-ui/" + SWAGGER_UI_VERSION))
                .toURI().toString());
        ContextHandler context = new ContextHandler();
        context.setContextPath("/docs");
        context.setHandler(rh);
        return context;
    }

}
