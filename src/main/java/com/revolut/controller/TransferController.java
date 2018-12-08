package com.revolut.controller;

import com.google.inject.Inject;
import com.revolut.domain.Account;
import com.revolut.exception.dto.ApiError;
import com.revolut.service.ITransferService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import javax.validation.constraints.NotNull;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

@Path("/api/transfer")
@Api(tags = "transfer", description = "Transfer money", protocols = "http, https")
public class TransferController {

    private final ITransferService transferService;

    @Inject
    public TransferController(ITransferService transferService) {
        this.transferService = transferService;
    }

    @POST
    @ApiOperation(value = "Transfer money from one account to another", response = Account.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK", response = Account.class),
            @ApiResponse(code = 403, message = "Forbidden", response = ApiError.class),
            @ApiResponse(code = 404, message = "Not found", response = ApiError.class)
    })
    public void transfer(@NotNull @FormParam("sourceId") String sourceId,
            @NotNull @FormParam("destinationId") String destId,
            @NotNull @FormParam("amount") Long amount) {
        transferService.transfer(sourceId, destId, amount);
    }
}
