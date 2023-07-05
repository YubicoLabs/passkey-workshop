package com.yubicolabs.bank_app.services.storage.mysql;

import java.time.Instant;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yubicolabs.bank_app.interfaces.AccountStorage;
import com.yubicolabs.bank_app.models.common.Account;
import com.yubicolabs.bank_app.models.dbo.mysql.AccountDBO;

@Component
public class AccountStorage_mysql implements AccountStorage {

  @Autowired(required = false)
  AccountStorageRepositoryMysql accountStorageRepositoryMysql;

  @Override
  public Account create(String userhandle, boolean isAdvancedProtection, double balance, Instant createTime)
      throws Exception {
    try {
      Account account = Account.builder().userHandle(userhandle)
          .advancedProtection(isAdvancedProtection)
          .balance(balance).createTime(createTime).build();

      AccountDBO newItem = AccountDBO.builder().userHandle(account.getUserHandle())
          .advancedProtection(account.isAdvancedProtection())
          .balance(account.getBalance()).createTime(account.getCreateTime().toEpochMilli()).build();

      accountStorageRepositoryMysql.save(newItem);
      return account;
    } catch (Exception e) {
      e.printStackTrace();
      throw new Exception("Account creation failed: " + e.getLocalizedMessage());
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
  public boolean processTransaction(int accountId, String type, double amount) {
    try {
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
      AccountDBO updateDBO = accountStorageRepositoryMysql
          .findById(new Long(accountId)).get();
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
