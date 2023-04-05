package com.yubicolabs.passkey_rp.services.storage;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.yubicolabs.passkey_rp.interfaces.AssertionRequestStorage;
import com.yubicolabs.passkey_rp.interfaces.AttestationRequestStorage;
import com.yubicolabs.passkey_rp.interfaces.CredentialStorage;

import lombok.Getter;

@Component
@Scope("singleton")
public class StorageInstance {

  @Autowired
  private AssertionRequestStorageFactoryBean assertionRequestStorageFactoryBean;

  @Autowired
  private AttestationRequestStorageFactoryBean attestationRequestStorageFactoryBean;

  @Autowired
  private CredentialRegistrationStorageFactoryBean credentialRegistrationStorageFactoryBean;

  @Getter
  private AssertionRequestStorage assertionRequestStorage;

  @Getter
  private AttestationRequestStorage attestationRequestStorage;

  @Getter
  private CredentialStorage credentialStorage;

  @PostConstruct
  private void setStorageInstance() {
    this.assertionRequestStorage = assertionRequestStorageFactoryBean.getObject();
    this.attestationRequestStorage = attestationRequestStorageFactoryBean.getObject();
    this.credentialStorage = credentialRegistrationStorageFactoryBean.getObject();
  }
}
