package com.yubicolabs.bank_app.services.storage.mysql;

import java.time.Instant;
import java.util.Collection;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yubicolabs.bank_app.interfaces.AccountTransactionStorage;
import com.yubicolabs.bank_app.models.common.AccountTransaction;
import com.yubicolabs.bank_app.models.dbo.mysql.AccountTransactionDBO;

@Component
public class AccountTransactionStorage_mysql implements AccountTransactionStorage {

  @Autowired(required = false)
  AccountTransactionRepositoryMysql accountTransactionRepositoryMysql;

  @Override
  public Collection<AccountTransaction> getAll(int accountId) {
    Collection<AccountTransactionDBO> accountTransactionList = accountTransactionRepositoryMysql.findAll();

    return accountTransactionList.stream()
        .filter(accountTransaction -> accountTransaction.getAccountId() == accountId)
        .map(accountTransactionDBO -> buildAccountTransaction(accountTransactionDBO))
        .collect(Collectors.toList());
  }

  @Override
  public AccountTransaction create(String type, double amount, String description, Boolean status, int accountId,
      Instant createTime) throws Exception {
    try {
      AccountTransactionDBO newItem = AccountTransactionDBO.builder()
          .type(type)
          .amount(amount)
          .description(description)
          .createTime(createTime.toEpochMilli())
          .status(status)
          .accountId(accountId)
          .build();

      accountTransactionRepositoryMysql.save(newItem);
      return buildAccountTransaction(newItem);
    } catch (Exception e) {
      e.printStackTrace();
      throw new Exception("There was an issue creating your transaction");
    }
  }

  private AccountTransaction buildAccountTransaction(AccountTransactionDBO dbo) {
    return AccountTransaction.builder()
        .type(dbo.getType())
        .amount(dbo.getAmount())
        .description(dbo.getDescription())
        .createTime(Instant.ofEpochMilli(dbo.getCreateTime()))
        .status(dbo.isStatus())
        .accountId(dbo.getAccountId())
        .id(dbo.getId()).build();
  }
}
