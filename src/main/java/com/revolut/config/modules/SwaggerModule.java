package com.revolut.config.modules;

import com.google.inject.Singleton;
import com.google.inject.servlet.ServletModule;
import com.revolut.config.SwaggerConfig;
import com.revolut.controller.SwaggerController;
import com.typesafe.config.Config;
import io.swagger.jaxrs.config.BeanConfig;
import io.swagger.jaxrs.listing.ApiListingResource;
import io.swagger.jaxrs.listing.SwaggerSerializers;

public class SwaggerModule extends ServletModule {

    private final Config config;

    public SwaggerModule(Config config) {
        this.config = config.getConfig("http");
    }

    @Override
    protected void configureServlets() {
        bind(SwaggerConfig.class).in(Singleton.class);
        bind(SwaggerController.class).in(Singleton.class);
        bind(ApiListingResource.class).in(Singleton.class);
        bind(SwaggerSerializers.class).in(Singleton.class);

        serve("").with(SwaggerConfig.class);
        BeanConfig beanConfig = new BeanConfig();
        beanConfig.setVersion("1.0");
        beanConfig.setSchemes(new String[]{"http"});
        beanConfig.setTitle("API documentation");
        beanConfig.setDescription("Api Documentation");
        beanConfig.setLicense("Apache 2.0");
        beanConfig.setLicenseUrl("http://www.apache.org/licenses/LICENSE-2.0");
        beanConfig.setHost(config.getString("host") + ":" + config.getInt("port"));
        beanConfig.setBasePath("/");
        beanConfig.setResourcePackage("com.revolut.controller");
        beanConfig.setScan(true);
    }
}
