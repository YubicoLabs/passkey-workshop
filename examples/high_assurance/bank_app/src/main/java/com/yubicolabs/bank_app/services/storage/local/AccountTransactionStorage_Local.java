package com.yubicolabs.bank_app.services.storage.local;

import java.time.Instant;
import java.util.Collection;
import java.util.HashSet;
import java.util.concurrent.ThreadLocalRandom;
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
  public AccountTransaction create(String type, double amount, String description, Boolean status, int accountId,
      Instant createTime) throws Exception {

    AccountTransaction new_item = AccountTransaction.builder()
        .type(type)
        .amount(amount)
        .description(description)
        .createTime(createTime)
        .status(status)
        .accountId(accountId)
        .id(ThreadLocalRandom.current().nextLong(100)).build();
    boolean didCreate = accountTransactionRepository.add(new_item);

    if (didCreate) {
      return new_item;
    } else {
      throw new Exception("There was an issue creating your transaction");
    }
  }
}
