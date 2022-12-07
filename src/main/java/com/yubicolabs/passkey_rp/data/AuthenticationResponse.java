package com.yubicolabs.passkey_rp.data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.yubico.webauthn.data.AuthenticatorAssertionResponse;
import com.yubico.webauthn.data.ByteArray;
import com.yubico.webauthn.data.ClientAssertionExtensionOutputs;
import com.yubico.webauthn.data.PublicKeyCredential;
import com.yubico.webauthn.data.PublicKeyCredentialRequestOptions;

import lombok.Value;

/*
 * Object sent by the client application containing the attested credential
 */
@Value
public class AuthenticationResponse {
  PublicKeyCredential<AuthenticatorAssertionResponse, ClientAssertionExtensionOutputs> assertionResponse;
  PublicKeyCredentialRequestOptions publicKeyCredentialRequestOptions;
  ByteArray requestId;

  @JsonCreator
  public AuthenticationResponse(
      @JsonProperty("requestId") ByteArray requestId,
      @JsonProperty("assertionResponse") PublicKeyCredential<AuthenticatorAssertionResponse, ClientAssertionExtensionOutputs> assertionResponse,
      @JsonProperty("publicKeyCredentialRequestOptions") PublicKeyCredentialRequestOptions publicKeyCredentialRequestOptions) {
    this.requestId = requestId;
    this.assertionResponse = assertionResponse;
    this.publicKeyCredentialRequestOptions = publicKeyCredentialRequestOptions;
  }
}
