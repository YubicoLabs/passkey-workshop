package com.yubicolabs.passkey_rp.services.storage;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Service;

import com.yubicolabs.passkey_rp.interfaces.AssertionRequestStorage;
import com.yubicolabs.passkey_rp.services.storage.mysql.AssertionRequestStorage_mysql;

@Service
public class AssertionRequestStorageFactoryBean implements FactoryBean<AssertionRequestStorage> {

  @Value("${datasource.type}")
  private String name;

  @Autowired(required = false)
  AssertionRequestStorage_mysql assertionRequestStorage_mysql;

  // @TODO - I need a way to abandon the mysql instance if the DB cannot connect
  public AssertionRequestStorage getObject() {
    if (name.equalsIgnoreCase("mysql")) {
      System.out.println("****** This item was called *******");
      return assertionRequestStorage_mysql;
    } else {
      return new AssertionRequestStorage_Local();
    }
  }

  @Override
  public Class<?> getObjectType() {
    // TODO Auto-generated method stub
    return null;
  }
}
