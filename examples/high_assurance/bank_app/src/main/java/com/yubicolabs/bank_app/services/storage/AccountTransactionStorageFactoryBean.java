package com.yubicolabs.bank_app.services.storage;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.yubicolabs.bank_app.interfaces.AccountTransactionStorage;
import com.yubicolabs.bank_app.services.storage.local.AccountTransactionStorage_Local;
import com.yubicolabs.bank_app.services.storage.mysql.AccountTransactionStorage_mysql;

public class AccountTransactionStorageFactoryBean implements FactoryBean<AccountTransactionStorage> {

  @Value("${datasource.type}")
  private String name;

  @Autowired(required = false)
  AccountTransactionStorage_mysql accountTransactionStorage_mysql;

  @Override
  public AccountTransactionStorage getObject() {
    if (name.equalsIgnoreCase("mysql")) {
      return accountTransactionStorage_mysql;
    } else {
      return new AccountTransactionStorage_Local();
    }
  }

  @Override
  public Class<?> getObjectType() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'getObjectType'");
  }

}
