package com.revolut.config.modules;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.revolut.config.JettyConfig;

public class JettyModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(JettyConfig.class).in(Singleton.class);
    }
}
