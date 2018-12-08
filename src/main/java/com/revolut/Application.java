package com.revolut;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.revolut.config.JettyConfig;
import com.revolut.config.modules.ConfigurationModule;
import com.revolut.config.modules.ControllerModule;
import com.revolut.config.modules.JettyModule;
import com.revolut.config.modules.SwaggerModule;
import com.revolut.domain.Account;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Application {

    private static final List<Account> ACCOUNTS = Collections.synchronizedList(new ArrayList<>());

    public static void main(String[] args) {
        final Config config = ConfigFactory.load();

        final Injector injector = Guice.createInjector(
                new ConfigurationModule(config, ACCOUNTS),
                new JettyModule(),
                new ControllerModule(),
                new SwaggerModule(config)
        );

        // launch http server
        final JettyConfig jetty = injector.getInstance(JettyConfig.class);
        try {
            jetty.start();
            jetty.join();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                jetty.stop();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
