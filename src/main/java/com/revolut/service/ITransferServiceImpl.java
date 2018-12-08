package com.revolut.service;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.revolut.domain.Account;
import com.revolut.exception.ForbiddenException;
import java.util.List;
import javax.inject.Named;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Singleton
public final class ITransferServiceImpl implements ITransferService {

    private final List<Account> accounts;
    private final IAccountService accountService;

    @Inject
    public ITransferServiceImpl(@Named("accounts") List<Account> accounts,
            IAccountService accountService) {
        this.accounts = accounts;
        this.accountService = accountService;
    }

    @Override
    public void transfer(String sourceId, String destId, Long amount) {
        if (sourceId.equals(destId)) {
            throw new ForbiddenException("You can not send money to yourself!");
        } else if (amount < 0) {
            throw new ForbiddenException("Amount can not be less than 0!");
        }
        synchronized (accounts) {
            Account from = accountService.getAccounts(sourceId);
            if (from.getBalance() < amount) {
                String message = String.format("Balance on account with id %s less than %d", from.getId(), amount);
                log.error(message);
                throw new ForbiddenException(message);
            } else {
                Account to = accountService.getAccounts(destId);
                from.setBalance(from.getBalance() - amount);
                to.setBalance(to.getBalance() + amount);
                log.info("Money in the amount {} successfully transfer from account {} to {}", amount, from.getId(),
                        to.getId());
            }
        }
    }
}
