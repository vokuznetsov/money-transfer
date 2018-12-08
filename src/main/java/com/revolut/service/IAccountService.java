package com.revolut.service;

import com.revolut.domain.Account;
import java.util.List;

public interface IAccountService {

    Account createAccount(String name, Long balance);

    Account getAccounts(String id);

    List<Account> getAccounts();

    Account modifyAccount(Account newAcc);

    void delete(String id);

}
