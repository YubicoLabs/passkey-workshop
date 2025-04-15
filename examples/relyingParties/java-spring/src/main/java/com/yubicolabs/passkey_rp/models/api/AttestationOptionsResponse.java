package com.yubicolabs.passkey_rp.models.api;

import com.yubico.webauthn.data.PublicKeyCredentialCreationOptions;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

/**
 * AttestationOptionsResponse
 */

@Builder
@AllArgsConstructor
@Value
public class AttestationOptionsResponse {
  private String requestId;
  private PublicKeyCredentialCreationOptions publicKey;
}
