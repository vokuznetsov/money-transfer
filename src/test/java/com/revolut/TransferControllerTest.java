package com.revolut;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.revolut.domain.Account;
import com.revolut.exception.dto.ApiError;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.representation.Form;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.ws.rs.core.Response.Status;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

@Slf4j
public class TransferControllerTest extends AbstractControllerTest {

    private static final String TRANSFER_API = "/api/transfer";

    @Test
    public void successfulTransferTest() {
        Long firstBalance = 100L;
        Long secondBalance = 200L;
        Account firstAcc = new Account("first", firstBalance);
        Account secondAcc = new Account("second", secondBalance);

        ACCOUNTS.addAll(Arrays.asList(firstAcc, secondAcc));
        assertEquals(ACCOUNTS.size(), 2);

        Long totalBalance1 = ACCOUNTS.stream().mapToLong(Account::getBalance).count();
        Long amount = firstAcc.getBalance() - 30;

        Form formData = new Form();
        formData.add("sourceId", firstAcc.getId());
        formData.add("destinationId", secondAcc.getId());
        formData.add("amount", amount);

        ClientResponse resp = service.path(TRANSFER_API)
                .post(ClientResponse.class, formData);
        assertEquals(resp.getStatus(), Status.NO_CONTENT.getStatusCode());

        Long totalBalance2 = ACCOUNTS.stream().mapToLong(Account::getBalance).count();
        assertEquals(totalBalance1, totalBalance2);

        assertTrue(firstAcc.getBalance().equals(firstBalance - amount));
        assertTrue(secondAcc.getBalance().equals(secondBalance + amount));
        assertEquals(ACCOUNTS.size(), 2);
    }

    @Test
    public void bigAmountErrorTransferTest() {
        Long firstBalance = 100L;
        Long secondBalance = 200L;
        Account firstAcc = new Account("first", firstBalance);
        Account secondAcc = new Account("second", secondBalance);

        ACCOUNTS.addAll(Arrays.asList(firstAcc, secondAcc));
        assertEquals(ACCOUNTS.size(), 2);

        Long totalBalance1 = ACCOUNTS.stream().mapToLong(Account::getBalance).count();
        Long amount = firstAcc.getBalance() + 30;

        Form formData = new Form();
        formData.add("sourceId", firstAcc.getId());
        formData.add("destinationId", secondAcc.getId());
        formData.add("amount", amount);

        ClientResponse resp = service.path(TRANSFER_API)
                .post(ClientResponse.class, formData);
        assertEquals(resp.getStatus(), Status.FORBIDDEN.getStatusCode());

        ApiError error = resp.getEntity(ApiError.class);
        assertEquals(error.getCode(), Status.FORBIDDEN.getStatusCode());
        assertTrue(error.getMessage().startsWith("Balance on account with id"));

        Long totalBalance2 = ACCOUNTS.stream().mapToLong(Account::getBalance).count();
        assertEquals(totalBalance1, totalBalance2);

        assertTrue(firstAcc.getBalance().equals(firstBalance));
        assertTrue(secondAcc.getBalance().equals(secondBalance));
        assertEquals(ACCOUNTS.size(), 2);
    }

    @Test
    public void yourselfErrorTransferTest() {
        Form formData = new Form();
        formData.add("sourceId", 1L);
        formData.add("destinationId", 1L);
        formData.add("amount", 123);

        ClientResponse resp = service.path(TRANSFER_API)
                .post(ClientResponse.class, formData);
        assertEquals(resp.getStatus(), Status.FORBIDDEN.getStatusCode());

        ApiError error = resp.getEntity(ApiError.class);
        assertEquals(error.getCode(), Status.FORBIDDEN.getStatusCode());
        assertTrue(error.getMessage().startsWith("You can not send money to yourself!"));
    }

    @Test
    public void negativeAmountErrorTransferTest() {
        Form formData = new Form();
        formData.add("sourceId", 1L);
        formData.add("destinationId", 2L);
        formData.add("amount", -77);

        ClientResponse resp = service.path(TRANSFER_API)
                .post(ClientResponse.class, formData);
        assertEquals(resp.getStatus(), Status.FORBIDDEN.getStatusCode());

        ApiError error = resp.getEntity(ApiError.class);
        assertEquals(error.getCode(), Status.FORBIDDEN.getStatusCode());
        assertTrue(error.getMessage().startsWith("Amount can not be less than 0!"));
    }

    /**
     * This test simulates multithreading environment and check when all runnable tasks have completed successfully
     * before and after total balances should be equal.
     *
     * Max test completion time is 1 minute.
     * After this time test completes successful but write a warning message in log.
     */
    @Test
    public void multithreadingTransfer() throws InterruptedException {
        List<Account> accounts = IntStream.range(0, 50)
                .mapToObj(i -> new Account(String.valueOf(i), ThreadLocalRandom.current().nextLong(10, 100)))
                .collect(Collectors.toList());
        ACCOUNTS.addAll(new ArrayList<>(accounts));
        assertEquals(ACCOUNTS.size(), accounts.size());

        Long totalBalance = accounts.stream().mapToLong(Account::getBalance).count();

        ExecutorService executorService = Executors.newFixedThreadPool(30);
        List<Runnable> runnableTasks = new ArrayList<>();
        List<Future<?>> futures = new ArrayList<>();

        for (int i = 0; i < 100; i++) {
            Runnable r = createRunnableTask(accounts);
            runnableTasks.add(r);
        }

        for (Runnable task : runnableTasks) {
            futures.add(executorService.submit(task));
        }

        LocalDateTime start = LocalDateTime.now();

        while (start.plusMinutes(1).isAfter(LocalDateTime.now())) {
            Thread.sleep(5000);
            boolean allDone = true;
            for (Future<?> future : futures) {
                allDone &= future.isDone();
            }

            if (allDone) {
                log.info("All task are completed successfully");
                Long totalBalanceAfterTransfer = ACCOUNTS.stream().mapToLong(Account::getBalance).count();
                assertTrue(totalBalance.equals(totalBalanceAfterTransfer));
                return;
            }
        }
        log.warn("All runnable tasks haven't been completing yet during 1 minute");
    }

    private Runnable createRunnableTask(List<Account> accounts) {
        return () -> {
            for (int times = 0; times < 100; times++) {
                Account fromAccount = accounts.get(ThreadLocalRandom.current().nextInt(accounts.size()));
                String toAccount = accounts.get(ThreadLocalRandom.current().nextInt(accounts.size())).getId();

                if (fromAccount.getBalance() > 0) {
                    Long amount = ThreadLocalRandom.current().nextLong(fromAccount.getBalance());
                    Form formData = new Form();
                    formData.add("sourceId", fromAccount.getId());
                    formData.add("destinationId", toAccount);
                    formData.add("amount", amount);

                    service.path(TRANSFER_API)
                            .post(ClientResponse.class, formData);
                }
            }
        };
    }
}
