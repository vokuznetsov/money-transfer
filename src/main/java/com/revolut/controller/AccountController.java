package com.revolut.controller;

import com.revolut.domain.Account;
import com.revolut.exception.dto.ApiError;
import com.revolut.service.IAccountService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.util.List;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/api/account")
@Produces(MediaType.APPLICATION_JSON)
@Api(tags = "account", description = "Operations with accounts", protocols = "http, https",
        consumes = "application/json", produces = "application/json")
public class AccountController {

    private final IAccountService accountService;

    @Inject
    public AccountController(IAccountService accountService) {
        this.accountService = accountService;
    }

    @POST
    @ApiOperation(value = "Create new user account", response = Account.class)
    @ApiResponses(@ApiResponse(code = 200, message = "OK", response = Account.class))
    public Account createAccount(@NotNull @FormParam("name") String name,
            @NotNull @FormParam("balance") Long balance) {
        return accountService.createAccount(name, balance);
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Get account by id", response = Account.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK", response = Account.class),
            @ApiResponse(code = 404, message = "Not found", response = ApiError.class)
    })
    public Account getAccount(@NotNull @PathParam("id") String id) {
        return accountService.getAccounts(id);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Get account by id", response = Account.class)
    @ApiResponses(@ApiResponse(code = 200, message = "OK", response = Account.class))
    public List<Account> getAccounts() {
        return accountService.getAccounts();
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Modify account", response = Account.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK", response = Account.class),
            @ApiResponse(code = 404, message = "Not found", response = ApiError.class)
    })
    public Account modifyAccount(@NotNull @Valid Account newAcc) {
        return accountService.modifyAccount(newAcc);
    }

    @DELETE
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Delete account by id", response = Account.class)
    public void deleteAccount(@NotNull @PathParam("id") String id) {
        accountService.delete(id);
    }
}
