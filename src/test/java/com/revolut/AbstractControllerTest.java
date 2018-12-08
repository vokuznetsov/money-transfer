package com.revolut;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.revolut.config.modules.ConfigurationModule;
import com.revolut.config.modules.ControllerModule;
import com.revolut.config.modules.JettyModule;
import com.revolut.domain.Account;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.container.grizzly2.GrizzlyServerFactory;
import com.sun.jersey.api.core.PackagesResourceConfig;
import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.core.spi.component.ioc.IoCComponentProviderFactory;
import com.sun.jersey.guice.spi.container.GuiceComponentProviderFactory;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.ws.rs.core.UriBuilder;
import lombok.extern.slf4j.Slf4j;
import org.glassfish.grizzly.http.server.HttpServer;
import org.junit.After;
import org.junit.Before;

@Slf4j
public class AbstractControllerTest {

    private static final String CONTROLLER_PACKAGE = "com.revolut.controller";
    private static final URI BASE_URI = getBaseURI();

    protected static final List<Account> ACCOUNTS = Collections.synchronizedList(new ArrayList<>());

    private HttpServer server;
    protected WebResource service;
    protected ObjectMapper mapper = new ObjectMapper();

    private static URI getBaseURI() {
        return UriBuilder.fromUri("http://localhost/").port(9998).build();
    }

    @Before
    public void startServer() throws IOException {
        log.info("Starting server...");
        Injector injector = configurationGuice();

        ResourceConfig rc = new PackagesResourceConfig(CONTROLLER_PACKAGE);
        IoCComponentProviderFactory ioc = new GuiceComponentProviderFactory(rc, injector);
        server = GrizzlyServerFactory.createHttpServer(BASE_URI, rc, ioc);

        Client client = Client.create(new DefaultClientConfig());
        service = client.resource(getBaseURI());

        ACCOUNTS.clear();
    }

    @After
    public void stopServer() {
        server.stop();
        log.info("Server stopped");
    }

    private Injector configurationGuice() {
        final Config config = ConfigFactory.load();

        return Guice.createInjector(new ConfigurationModule(config, ACCOUNTS),
                new JettyModule(),
                new ControllerModule());
    }
}
