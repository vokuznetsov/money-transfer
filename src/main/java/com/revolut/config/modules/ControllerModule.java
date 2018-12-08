package com.revolut.config.modules;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Singleton;
import com.google.inject.servlet.ServletModule;
import com.revolut.controller.AccountController;
import com.revolut.controller.TransferController;
import com.revolut.exception.mapper.ForbiddenExceptionMapper;
import com.revolut.exception.mapper.NotFoundExceptionMapper;
import com.revolut.filter.CorsFilter;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import org.codehaus.jackson.jaxrs.JacksonJsonProvider;

public class ControllerModule extends ServletModule {


    @Override
    protected void configureServlets() {
        bind(AccountController.class).in(Singleton.class);
        bind(TransferController.class).in(Singleton.class);

        bind(MessageBodyReader.class).to(JacksonJsonProvider.class);
        bind(MessageBodyWriter.class).to(JacksonJsonProvider.class);

        bind(NotFoundExceptionMapper.class).in(Singleton.class);
        bind(ForbiddenExceptionMapper.class).in(Singleton.class);

        serve("/*").with(GuiceContainer.class,
                ImmutableMap.of("com.sun.jersey.api.json.POJOMappingFeature", "true"));

        bind(CorsFilter.class).in(Singleton.class);
        filter("/*").through(CorsFilter.class);
    }
}
