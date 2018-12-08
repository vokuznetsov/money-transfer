package com.revolut.config;

import io.swagger.jaxrs.config.SwaggerContextService;
import io.swagger.models.Swagger;
import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServlet;

public class SwaggerConfig extends HttpServlet {

    @Override
    public void init(final ServletConfig config) {
        Swagger swagger = new Swagger();
        new SwaggerContextService().withServletConfig(config).updateSwagger(swagger);
    }
}
