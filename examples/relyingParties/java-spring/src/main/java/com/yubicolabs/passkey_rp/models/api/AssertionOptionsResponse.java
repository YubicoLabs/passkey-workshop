package com.yubicolabs.passkey_rp.models.api;

import com.yubico.webauthn.data.PublicKeyCredentialRequestOptions;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

/**
 * AssertionOptionsResponse
 */

@Builder
@AllArgsConstructor
@Value
public class AssertionOptionsResponse {
  private String requestId;
  private PublicKeyCredentialRequestOptions publicKey;
  private String errorMessage;
}
