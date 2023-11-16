package com.yubicolabs.passkey_rp.services.storage;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.yubicolabs.passkey_rp.interfaces.AdvancedProtectionStatusStorage;
import com.yubicolabs.passkey_rp.services.storage.local.AdvancedProtectionStatusStorage_Local;
import com.yubicolabs.passkey_rp.services.storage.mysql.AdvancedProtectionStatusReepository_mysql;

@Service
public class AdvancedProtectionStatusStorageFactoryBean implements FactoryBean<AdvancedProtectionStatusStorage> {
  @Value("${datasource.type}")
  private String name;

  @Autowired(required = false)
  AdvancedProtectionStatusReepository_mysql advancedProtectionStatusReepository_mysql;

  public AdvancedProtectionStatusStorage getObject() {
    if (name.equalsIgnoreCase("mysql")) {
      return advancedProtectionStatusReepository_mysql;
    } else {
      return new AdvancedProtectionStatusStorage_Local();
    }
  }

  @Override
  public Class<?> getObjectType() {
    return null;
  }

}
