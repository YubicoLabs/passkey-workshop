package com.yubicolabs.passkey_rp.services.storage;

import org.springframework.stereotype.Service;

import com.yubicolabs.passkey_rp.interfaces.CredentialStorage;
import com.yubicolabs.passkey_rp.services.storage.local.CredentialStorage_Local;
import com.yubicolabs.passkey_rp.services.storage.mysql.CredentialRegistrationStorage_mysql;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

@Service
public class CredentialRegistrationStorageFactoryBean implements FactoryBean<CredentialStorage> {

  @Value("${datasource.type}")
  private String name;

  @Autowired(required = false)
  CredentialRegistrationStorage_mysql credentialRegistrationStorage_mysql;

  public CredentialStorage getObject() {
    if (name.equalsIgnoreCase("mysql")) {
      return credentialRegistrationStorage_mysql;
    } else {
      return new CredentialStorage_Local();
    }
  }

  @Override
  public Class<?> getObjectType() {
    // TODO Auto-generated method stub
    return null;
  }

}
