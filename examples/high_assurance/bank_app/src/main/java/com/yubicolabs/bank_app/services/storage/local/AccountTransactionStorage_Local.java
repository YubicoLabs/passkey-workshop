package com.yubicolabs.bank_app.services.storage.local;

import java.util.Collection;
import java.util.HashSet;
import java.util.stream.Collectors;

import com.yubicolabs.bank_app.interfaces.AccountTransactionStorage;
import com.yubicolabs.bank_app.models.common.AccountTransaction;

public class AccountTransactionStorage_Local implements AccountTransactionStorage {

  private Collection<AccountTransaction> accountTransactionRepository = new HashSet<AccountTransaction>();

  @Override
  public Collection<AccountTransaction> getAll(int accountId) {
    return accountTransactionRepository.stream()
        .filter(accountTransaction -> accountTransaction.getAccountId() == accountId)
        .collect(Collectors.toList());
  }

  @Override
  public boolean create(AccountTransaction accountTransaction) {
    return accountTransactionRepository.add(accountTransaction);
  }

}
