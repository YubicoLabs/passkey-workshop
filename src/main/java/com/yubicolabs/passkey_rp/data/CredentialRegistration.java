package com.yubicolabs.passkey_rp.data;

import java.time.Instant;
import java.util.Optional;

import com.yubico.webauthn.RegisteredCredential;
import com.yubico.webauthn.data.UserIdentity;

import lombok.Builder;
import lombok.Value;

/*
 * Object containing credential information that is stored in the CredentialStorage DB
 */
@Value
@Builder
public class CredentialRegistration {
  long signatureCounter;
  UserIdentity userIdentity;
  Optional<String> credentialNickname;
  Instant registrationTime;
  Instant lastUsedTime;
  Instant lastUpdateTime;
  RegisteredCredential credential;
  RegistrationRequest registrationRequest;

}
