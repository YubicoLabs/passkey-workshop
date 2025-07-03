package com.yubicolabs.passkey_rp.services.storage;

import org.springframework.stereotype.Service;

import com.yubicolabs.passkey_rp.interfaces.AdvancedProtectionStatusStorage;
import com.yubicolabs.passkey_rp.interfaces.AssertionRequestStorage;
import com.yubicolabs.passkey_rp.interfaces.AttestationRequestStorage;
import com.yubicolabs.passkey_rp.interfaces.CredentialStorage;

import lombok.Getter;

@Service
public class StorageInstance {

  @Getter
  private AssertionRequestStorage assertionRequestStorage;

  @Getter
  private AttestationRequestStorage attestationRequestStorage;

  @Getter
  private CredentialStorage credentialStorage;

  @Getter
  private AdvancedProtectionStatusStorage advancedProtectionStatusStorage;

  public StorageInstance(AttestationRequestStorage attestationRequestStorage,
      AssertionRequestStorage assertionRequestStorage, CredentialStorage credentialStorage,
      AdvancedProtectionStatusStorage advancedProtectionStatusStorage) {
    this.attestationRequestStorage = attestationRequestStorage;
    this.assertionRequestStorage = assertionRequestStorage;
    this.credentialStorage = credentialStorage;
    this.advancedProtectionStatusStorage = advancedProtectionStatusStorage;
  }
}
