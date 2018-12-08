package com.revolut;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.revolut.domain.Account;
import com.revolut.exception.dto.ApiError;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.representation.Form;
import java.util.List;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

@Slf4j
public class AccountControllerTest extends AbstractControllerTest {

    private static final String ACCOUNT_API = "/api/account";

    @Test
    public void createAccountTest() {
        String name = "first";
        Long balance = 100L;

        Form formData = new Form();
        formData.add("name", name);
        formData.add("balance", String.valueOf(balance));

        ClientResponse resp = service.path(ACCOUNT_API)
                .post(ClientResponse.class, formData);
        assertEquals(resp.getStatus(), Status.OK.getStatusCode());

        Account account = resp.getEntity(Account.class);
        assertNotNull(account.getId());
        assertEquals(account.getName(), name);
        assertEquals(account.getBalance(), balance);

        assertEquals(ACCOUNTS.size(), 1);
    }

    @Test
    public void getAccountTest() {
        Account account = new Account("first", 100L);
        ACCOUNTS.add(account);
        assertEquals(ACCOUNTS.size(), 1);

        // success
        ClientResponse respSuccess = service.path(ACCOUNT_API)
                .path(account.getId())
                .get(ClientResponse.class);
        assertEquals(respSuccess.getStatus(), Status.OK.getStatusCode());

        Account entity = respSuccess.getEntity(Account.class);
        assertTrue(account.equals(entity));

        // not found
        ClientResponse respError = service.path(ACCOUNT_API)
                .path("123")
                .get(ClientResponse.class);
        checkNotFoundError(respError);
    }

    @Test
    public void getAccountsTest() {
        ACCOUNTS.add(new Account("first", 100L));
        ACCOUNTS.add(new Account("second", 200L));
        assertEquals(ACCOUNTS.size(), 2);

        ClientResponse resp = service.path(ACCOUNT_API)
                .get(ClientResponse.class);
        assertEquals(resp.getStatus(), Status.OK.getStatusCode());

        List<Account> text = resp.getEntity(new GenericType<List<Account>>() {
        });

        assertEquals(200, resp.getStatus());
        assertEquals(text.size(), 2);
    }

    @Test
    public void modifyAccount() throws JsonProcessingException {
        Account oldAcc = new Account("first", 100L);
        ACCOUNTS.add(oldAcc);
        assertEquals(ACCOUNTS.size(), 1);

        Account newAccSuccess = new Account(oldAcc.getId(), "second", 999L);

        // success
        ClientResponse resp = service.path(ACCOUNT_API)
                .type(MediaType.APPLICATION_JSON)
                .entity(mapper.writeValueAsString(newAccSuccess))
                .put(ClientResponse.class);
        assertEquals(resp.getStatus(), Status.OK.getStatusCode());

        Account entity = resp.getEntity(Account.class);
        assertTrue(newAccSuccess.equals(entity));

        assertEquals(ACCOUNTS.size(), 1);

        // not found
        Account newAccError = new Account("1231", "second", 999L);

        ClientResponse respError = service.path(ACCOUNT_API)
                .type(MediaType.APPLICATION_JSON)
                .entity(mapper.writeValueAsString(newAccError))
                .put(ClientResponse.class);
        checkNotFoundError(respError);
    }

    @Test
    public void deleteAccountTest() {
        Account account = new Account("first", 100L);
        ACCOUNTS.add(account);
        assertEquals(ACCOUNTS.size(), 1);

        // success
        ClientResponse respSuccess = service.path(ACCOUNT_API)
                .path(account.getId())
                .delete(ClientResponse.class);

        assertEquals(respSuccess.getStatus(), Status.NO_CONTENT.getStatusCode());
        assertEquals(ACCOUNTS.size(), 0);

        // not found
        ClientResponse respError = service.path(ACCOUNT_API)
                .path("12312")
                .delete(ClientResponse.class);

        checkNotFoundError(respError);
    }

    private void checkNotFoundError(ClientResponse respError) {
        assertEquals(respError.getStatus(), Status.NOT_FOUND.getStatusCode());

        ApiError error = respError.getEntity(ApiError.class);
        assertEquals(error.getCode(), Status.NOT_FOUND.getStatusCode());
        assertTrue(error.getMessage().equals("Account didn't found"));
    }
}
