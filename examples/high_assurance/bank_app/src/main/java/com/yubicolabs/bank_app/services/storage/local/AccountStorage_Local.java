package com.yubicolabs.bank_app.services.storage.local;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.stream.Collectors;

import com.yubicolabs.bank_app.interfaces.AccountStorage;
import com.yubicolabs.bank_app.models.common.Account;
import com.yubicolabs.bank_app.models.common.AccountTransaction;

public class AccountStorage_Local implements AccountStorage {

  private Collection<Account> accountRepository = new HashSet<Account>();

  @Override
  public boolean create(Account account) {
    return accountRepository.add(account);
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
  public boolean processTransaction(AccountTransaction transaction) {
    double newBalance = 0;
    double originalBalance = get(transaction.getAccountId()).get().getBalance();
    if (transaction.getType().equals("deposit")) {
      newBalance = originalBalance + transaction.getAmount();
    } else if (transaction.getType().equals("withdraw")) {
      newBalance = originalBalance - transaction.getAmount();
    } else {
      return false;
    }

    double newBalance_final = newBalance;

    accountRepository.stream()
        .filter(account -> account.getId() == transaction.getAccountId())
        .forEach(account -> account.setBalance(newBalance_final));

    return get(transaction.getAccountId()).get().getBalance() == newBalance_final;
  }

}
