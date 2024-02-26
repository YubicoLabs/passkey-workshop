package com.yubicolabs.bank_app.services.storage;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.yubicolabs.bank_app.interfaces.AccountStorage;
import com.yubicolabs.bank_app.interfaces.AccountTransactionStorage;

import lombok.Getter;

@Component
@Scope("singleton")
public class StorageInstance {

  @Autowired
  private AccountStorageFactoryBean accountStorageFactoryBean;

  @Autowired
  private AccountTransactionStorageFactoryBean accountTransactionStorageFactoryBean;

  @Getter
  private AccountStorage accountStorage;

  @Getter
  private AccountTransactionStorage accountTransactionStorage;

  @PostConstruct
  private void setStorageInstance() {
    this.accountStorage = accountStorageFactoryBean.getObject();
    this.accountTransactionStorage = accountTransactionStorageFactoryBean.getObject();
  }
}
