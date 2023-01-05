package com.yubicolabs.passkey_rp.Services;

import com.yubicolabs.passkey_rp.interfaces.AssertionRequestStorage;
import com.yubicolabs.passkey_rp.interfaces.CredentialStorage;
import com.yubicolabs.passkey_rp.interfaces.RegistrationRequestStorage;

import lombok.Value;

/*
 * Global singleton containing references to storage operations
 */
@Value
public class StorageInstance {
  AssertionRequestStorage assertionRequestStorage = new AssertionRequestStorageLocal();
  RegistrationRequestStorage registrationRequestStorage = new RegistrationRequestStorageLocal();
  CredentialStorage credentialStorage = new CredentialStorageLocal();
}
