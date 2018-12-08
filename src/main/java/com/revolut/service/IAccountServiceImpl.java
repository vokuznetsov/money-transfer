package com.revolut.service;

import com.google.inject.Singleton;
import com.revolut.domain.Account;
import com.revolut.exception.NotFoundException;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Named;

@Singleton
public final class IAccountServiceImpl implements IAccountService {

    private final List<Account> accounts;

    @Inject
    public IAccountServiceImpl(@Named("accounts") List<Account> accounts) {
        this.accounts = accounts;
    }

    @Override
    public Account createAccount(String name, Long balance) {
        Account account = new Account(name, balance);
        accounts.add(account);
        return account;
    }

    @Override
    public Account getAccounts(String id) {
        synchronized (accounts) {
            return accounts.stream()
                    .filter(account -> account.getId().equals(id))
                    .findFirst().orElseThrow(() -> new NotFoundException("Account didn't found"));
        }
    }

    @Override
    public List<Account> getAccounts() {
        synchronized (accounts) {
            return accounts;
        }
    }

    @Override
    public Account modifyAccount(Account newAcc) {
        synchronized (accounts) {
            return accounts.stream()
                    .filter(account -> account.getId().equals(newAcc.getId()))
                    .peek(account -> account.setName(newAcc.getName()))
                    .peek(account -> account.setBalance(newAcc.getBalance()))
                    .findFirst().orElseThrow(() -> new NotFoundException("Account didn't found"));
        }
    }

    @Override
    public void delete(String id) {
        synchronized (accounts) {
            Account account = accounts.stream()
                    .filter(acc -> acc.getId().equals(id))
                    .findFirst().orElseThrow(() -> new NotFoundException("Account didn't found"));
            accounts.remove(account);
        }
    }
}
