package com.yubicolabs.passkey_rp.models.common;

import com.yubico.webauthn.AssertionRequest;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
public class AssertionOptions {

  @Getter
  AssertionRequest assertionRequest;

  @Getter
  @Setter
  Boolean isActive;

  @Getter
  String requestId;
}
