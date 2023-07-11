package com.yubicolabs.bank_app.services.storage;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.yubicolabs.bank_app.interfaces.AccountStorage;
import com.yubicolabs.bank_app.services.storage.local.AccountStorage_Local;
import com.yubicolabs.bank_app.services.storage.mysql.AccountStorage_mysql;

@Service
public class AccountStorageFactoryBean implements FactoryBean<AccountStorage> {

  @Value("${datasource.type}")
  private String name;

  @Autowired(required = false)
  AccountStorage_mysql accountStorage_mysql;

  public AccountStorage getObject() {
    if (name.equalsIgnoreCase("mysql")) {
      return accountStorage_mysql;
    } else {
      return new AccountStorage_Local();
    }
  }

  @Override
  public Class<?> getObjectType() {
    return null;
  }

}
