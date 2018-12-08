package com.revolut.exception.mapper;

import com.revolut.exception.ForbiddenException;
import com.revolut.exception.dto.ApiError;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class ForbiddenExceptionMapper implements ExceptionMapper<ForbiddenException> {

    @Override
    public Response toResponse(ForbiddenException ex) {
        ApiError entity = new ApiError(Status.FORBIDDEN.getStatusCode(), ex.getMessage());
        return Response.status(Status.FORBIDDEN)
                .entity(entity)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}
