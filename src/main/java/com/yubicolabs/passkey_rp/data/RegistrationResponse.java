package com.yubicolabs.passkey_rp.data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.yubico.webauthn.data.AuthenticatorAttestationResponse;
import com.yubico.webauthn.data.ByteArray;
import com.yubico.webauthn.data.ClientRegistrationExtensionOutputs;
import com.yubico.webauthn.data.PublicKeyCredential;
import com.yubico.webauthn.data.PublicKeyCredentialCreationOptions;

import lombok.Value;

/*
 * Response sent by the client application containing the new credentials
 */
@Value
public class RegistrationResponse {
  PublicKeyCredential<AuthenticatorAttestationResponse, ClientRegistrationExtensionOutputs> makeCredentialResponse;
  PublicKeyCredentialCreationOptions publicKeyCredentialCreationOptions;
  ByteArray requestId;

  @JsonCreator
  public RegistrationResponse(
      @JsonProperty("requestId") ByteArray requestId,
      @JsonProperty("makeCredentialResponse") PublicKeyCredential<AuthenticatorAttestationResponse, ClientRegistrationExtensionOutputs> makeCredentialResponse,
      @JsonProperty("publicKeyCredentialCreationOptions") PublicKeyCredentialCreationOptions publicKeyCredentialCreationOptions) {
    this.requestId = requestId;
    this.makeCredentialResponse = makeCredentialResponse;
    this.publicKeyCredentialCreationOptions = publicKeyCredentialCreationOptions;
  }

}
