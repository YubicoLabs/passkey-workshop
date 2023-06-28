package com.yubicolabs.bank_app.services.storage.mysql;

import java.time.Instant;
import java.util.Collection;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import com.yubicolabs.bank_app.interfaces.AccountTransactionStorage;
import com.yubicolabs.bank_app.models.common.AccountTransaction;
import com.yubicolabs.bank_app.models.dbo.mysql.AccountTransactionDBO;

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
  public boolean create(AccountTransaction accountTransaction) {
    try {
      AccountTransactionDBO newItem = AccountTransactionDBO.builder()
          .type(accountTransaction.getType())
          .amount(accountTransaction.getAmount())
          .description(accountTransaction.getDescription())
          .createTime(accountTransaction.getCreateTime().toEpochMilli())
          .status(accountTransaction.getStatus())
          .accountId(accountTransaction.getAccountId())
          .build();

      accountTransactionRepositoryMysql.save(newItem);
      return true;
    } catch (Exception e) {
      e.printStackTrace();
      System.out.println("[ERROR]: Error adding transaction to MySQL");
      return false;
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
