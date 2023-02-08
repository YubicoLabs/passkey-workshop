package com.yubicolabs.passkey_rp.models.common;

import java.time.Instant;
import java.util.Optional;

import com.yubico.webauthn.RegisteredCredential;
import com.yubico.webauthn.data.UserIdentity;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class CredentialRegistration {
  UserIdentity userIdentity;
  Optional<String> credentialNickname;
  Instant registrationTime;
  Instant lastUsedTime;
  Instant lastUpdateTime;
  RegisteredCredential credential;
}
