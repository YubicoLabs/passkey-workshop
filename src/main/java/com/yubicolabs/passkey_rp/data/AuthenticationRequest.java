package com.yubicolabs.passkey_rp.data;

import com.yubico.webauthn.data.ByteArray;
import com.yubico.webauthn.data.PublicKeyCredentialRequestOptions;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/*
 * Object sent to the client application to begin authentication
 */
@Builder
public class AuthenticationRequest {

  @Getter
  PublicKeyCredentialRequestOptions publicKeyCredentialRequestOptions;

  @Getter
  ByteArray requestId;

  @Getter
  String publicKey;

  @Getter
  @Setter
  Boolean isActive;
}
