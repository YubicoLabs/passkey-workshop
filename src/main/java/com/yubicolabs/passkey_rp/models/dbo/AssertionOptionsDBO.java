package com.yubicolabs.passkey_rp.models.dbo;

import com.yubico.webauthn.AssertionRequest;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
public class AssertionOptionsDBO {

  @Getter
  AssertionRequest assertionRequest;

  @Getter
  @Setter
  Boolean isActive;

  @Getter
  String requestId;
}
