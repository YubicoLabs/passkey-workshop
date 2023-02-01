package com.yubicolabs.passkey_rp.models.dbo;

import com.yubico.webauthn.data.ByteArray;
import com.yubico.webauthn.data.PublicKeyCredentialCreationOptions;
import com.yubicolabs.passkey_rp.models.api.AttestationOptionsResponse;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
public class AttestationOptionsDBO {
  @Getter
  PublicKeyCredentialCreationOptions pkc;

  @Getter
  @Setter
  Boolean isActive;

  @Getter
  String requestId;
}
