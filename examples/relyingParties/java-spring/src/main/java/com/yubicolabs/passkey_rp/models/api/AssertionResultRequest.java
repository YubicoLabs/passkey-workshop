package com.yubicolabs.passkey_rp.models.api;

import com.yubico.webauthn.data.AuthenticatorAssertionResponse;
import com.yubico.webauthn.data.ClientAssertionExtensionOutputs;
import com.yubico.webauthn.data.PublicKeyCredential;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * AssertionResultRequest
 */

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class AssertionResultRequest {
  private String requestId;
  private PublicKeyCredential<AuthenticatorAssertionResponse, ClientAssertionExtensionOutputs> assertionResult;
}
