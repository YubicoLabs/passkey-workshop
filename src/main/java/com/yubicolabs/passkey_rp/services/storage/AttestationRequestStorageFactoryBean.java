package com.yubicolabs.passkey_rp.services.storage;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.yubicolabs.passkey_rp.interfaces.AttestationRequestStorage;
import com.yubicolabs.passkey_rp.services.storage.local.AttestationRequestStorage_Local;
import com.yubicolabs.passkey_rp.services.storage.mysql.AttestationRequestStorage_mysql;

@Service
public class AttestationRequestStorageFactoryBean implements FactoryBean<AttestationRequestStorage> {

  @Value("${datasource.type}")
  private String name;

  @Autowired(required = false)
  AttestationRequestStorage_mysql attestationRequestStorage_mysql;

  public AttestationRequestStorage getObject() {
    if (name.equalsIgnoreCase("mysql")) {
      return attestationRequestStorage_mysql;
    } else {
      return new AttestationRequestStorage_Local();
    }
  }

  @Override
  public Class<?> getObjectType() {
    // TODO Auto-generated method stub
    return null;
  }
}
