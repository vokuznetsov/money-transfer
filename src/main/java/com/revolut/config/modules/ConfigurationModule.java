package com.revolut.config.modules;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.revolut.domain.Account;
import com.typesafe.config.Config;
import java.util.List;

public class ConfigurationModule extends AbstractModule {

    private final Config config;
    private final List<Account> accounts;

    public ConfigurationModule(Config config, List<Account> accounts) {
        this.config = config;
        this.accounts = accounts;
    }

    @Override
    protected void configure() {
        bind(Config.class).toInstance(config);
        install(new ComponentScanModule("com.revolut", Singleton.class));
    }

    @Provides
    @Named("accounts")
    private List<Account> provideAccountList() {
        return accounts;
    }
}
