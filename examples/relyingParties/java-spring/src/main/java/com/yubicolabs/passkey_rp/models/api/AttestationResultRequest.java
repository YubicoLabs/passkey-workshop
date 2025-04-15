package com.yubicolabs.passkey_rp.models.api;

import com.yubico.webauthn.data.AuthenticatorAttestationResponse;
import com.yubico.webauthn.data.ClientRegistrationExtensionOutputs;
import com.yubico.webauthn.data.PublicKeyCredential;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * AttestationResultRequest
 */

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class AttestationResultRequest {
  private String requestId;
  private PublicKeyCredential<AuthenticatorAttestationResponse, ClientRegistrationExtensionOutputs> makeCredentialResult;
}
