package com.yubicolabs.passkey_rp.models.common;

import com.yubico.webauthn.data.PublicKeyCredentialCreationOptions;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
public class AttestationOptions {
  @Getter
  PublicKeyCredentialCreationOptions attestationRequest;

  @Getter
  @Setter
  Boolean isActive;

  @Getter
  String requestId;
}
