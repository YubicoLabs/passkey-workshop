package com.yubicolabs.bank_app.services.storage.local;

import java.time.Instant;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import com.yubicolabs.bank_app.interfaces.AccountStorage;
import com.yubicolabs.bank_app.models.common.Account;

public class AccountStorage_Local implements AccountStorage {

  private Collection<Account> accountRepository = new HashSet<Account>();

  @Override
  public Account create(String userhandle, boolean isAdvancedProtection, double balance, Instant createTime)
      throws Exception {

    Account new_account = Account.builder()
        .userHandle(userhandle)
        .advancedProtection(isAdvancedProtection)
        .balance(balance)
        .createTime(createTime)
        .id(ThreadLocalRandom.current().nextLong(100)).build();
    boolean didCreate = accountRepository.add(new_account);
    if (didCreate) {
      return new_account;
    } else {
      throw new Exception("Account creation failed");
    }
  }

  @Override
  public Collection<Account> getAll(String userHandle) {
    return accountRepository.stream()
        .filter(account -> account.getUserHandle().equals(userHandle))
        .collect(Collectors.toList());
  }

  @Override
  public Optional<Account> get(int accountId) {
    return accountRepository.stream()
        .filter(account -> account.getId() == accountId)
        .findFirst();
  }

  @Override
  public boolean setAdvancedProtection(int accountId, boolean setting) {
    accountRepository.stream()
        .filter(account -> account.getId() == accountId)
        .forEach(account -> account.setAdvancedProtection(setting));
    return setting == get(accountId).get().isAdvancedProtection();
  }

  @Override
  public boolean processTransaction(int accountId, String type, double amount) {
    double newBalance = 0;
    double originalBalance = get(accountId).get().getBalance();
    if (type.equals("deposit")) {
      newBalance = originalBalance + amount;
    } else if (type.equals("withdraw")) {
      newBalance = originalBalance - amount;
    } else {
      return false;
    }

    double newBalance_final = newBalance;

    accountRepository.stream()
        .filter(account -> account.getId() == accountId)
        .forEach(account -> account.setBalance(newBalance_final));

    return get(accountId).get().getBalance() == newBalance_final;
  }

}
