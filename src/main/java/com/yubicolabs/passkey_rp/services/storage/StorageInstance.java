package com.yubicolabs.passkey_rp.services.storage;

import com.yubicolabs.passkey_rp.interfaces.AssertionRequestStorage;
import com.yubicolabs.passkey_rp.interfaces.AttestationRequestStorage;
import com.yubicolabs.passkey_rp.interfaces.CredentialStorage;

import lombok.Value;

@Value
public class StorageInstance {
  AssertionRequestStorage assertionRequestStorage = new AssertionRequestStorage_Local();
  AttestationRequestStorage attestationRequestStorage = new AttestationRequestStorage_Local();
  CredentialStorage credentialStorage = new CredentialStorage_Local();
}
