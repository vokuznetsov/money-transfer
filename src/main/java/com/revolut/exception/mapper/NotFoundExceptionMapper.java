package com.revolut.exception.mapper;

import com.revolut.exception.NotFoundException;
import com.revolut.exception.dto.ApiError;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Convert exception to JSON.
 */
@Provider
public class NotFoundExceptionMapper implements ExceptionMapper<NotFoundException> {

    @Override
    public Response toResponse(NotFoundException ex) {
        ApiError entity = new ApiError(Status.NOT_FOUND.getStatusCode(), ex.getMessage());
        return Response.status(Status.NOT_FOUND)
                .entity(entity)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}
