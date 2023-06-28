package com.yubicolabs.bank_app.services.storage.mysql;

import java.time.Instant;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import com.yubicolabs.bank_app.interfaces.AccountStorage;
import com.yubicolabs.bank_app.models.common.Account;
import com.yubicolabs.bank_app.models.common.AccountTransaction;
import com.yubicolabs.bank_app.models.dbo.mysql.AccountDBO;

public class AccountStorage_mysql implements AccountStorage {

  @Autowired(required = false)
  AccountStorageRepositoryMysql accountStorageRepositoryMysql;

  @Override
  public boolean create(Account account) {
    try {
      AccountDBO newItem = AccountDBO.builder().userHandle(account.getUserHandle())
          .advancedProtection(account.isAdvancedProtection())
          .balance(account.getBalance()).createTime(account.getCreateTime().toEpochMilli()).build();
      accountStorageRepositoryMysql.save(newItem);
      return true;
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
  }

  @Override
  public Collection<Account> getAll(String userHandle) {
    Collection<AccountDBO> accountList = accountStorageRepositoryMysql.findAll();

    return accountList.stream()
        .filter(account -> account.getUserHandle().equals(userHandle))
        .map(accountDBO -> buildAccount(accountDBO))
        .collect(Collectors.toList());
  }

  @Override
  public Optional<Account> get(int accountId) {
    Optional<AccountDBO> maybeItem = accountStorageRepositoryMysql.findById(new Long(accountId));

    if (maybeItem.isPresent()) {
      return Optional.of(buildAccount(maybeItem.get()));
    } else {
      return Optional.ofNullable(null);
    }
  }

  @Override
  public boolean setAdvancedProtection(int accountId, boolean setting) {
    try {
      /**
       * Check if account is valid
       */
      Optional<Account> maybeAccount = get(accountId);

      if (maybeAccount.isPresent()) {
        AccountDBO updateDBO = accountStorageRepositoryMysql
            .findById(new Long(accountId)).get();

        updateDBO.setAdvancedProtection(setting);

        AccountDBO newDBO = accountStorageRepositoryMysql.save(updateDBO);

        if (newDBO.getAdvancedProtection() == setting) {
          return true;
        } else {
          return false;
        }

      } else {
        return false;
      }
    } catch (Exception e) {
      e.printStackTrace();
      System.out.println("[Error]: Setting advanced protection failed at MySQL");
      return false;
    }
  }

  @Override
  public boolean processTransaction(AccountTransaction transaction) {
    try {
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
      AccountDBO updateDBO = accountStorageRepositoryMysql
          .findById(new Long(transaction.getAccountId())).get();
      updateDBO.setBalance(newBalance_final);

      AccountDBO newDBO = accountStorageRepositoryMysql.save(updateDBO);

      return newDBO.getBalance() == newBalance_final;
    } catch (Exception e) {
      e.printStackTrace();
      System.out.println("[Error]: Processing transaction failed at MySQL");
      return false;
    }
  }

  private Account buildAccount(AccountDBO accountDBO) {
    return Account.builder()
        .userHandle(accountDBO.getUserHandle())
        .advancedProtection(accountDBO.getAdvancedProtection())
        .balance(accountDBO.getBalance())
        .createTime(Instant.ofEpochMilli(accountDBO.getCreateTime()))
        .id(accountDBO.getId())
        .build();
  }

}
