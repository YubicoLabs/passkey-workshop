package com.yubicolabs.passkey_rp.data;

import com.yubico.webauthn.data.ByteArray;
import com.yubico.webauthn.data.PublicKeyCredentialCreationOptions;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * Object sent to the client application to begin registrations
 */
@Builder
public class RegistrationRequest {
  @Getter
  PublicKeyCredentialCreationOptions publicKeyCredentialCreationOptions;

  @Getter
  ByteArray requestId;

  @Getter
  String publicKey;

  @Getter
  @Setter
  Boolean isActive;
}
