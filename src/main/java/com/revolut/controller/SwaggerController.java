package com.revolut.controller;

import java.net.URI;
import java.net.URISyntaxException;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path("/")
public class SwaggerController {

    @GET
    public Response redirect() throws URISyntaxException {
        return Response.temporaryRedirect(new URI("../docs?url=%2Fswagger.json")).build();
    }
}
