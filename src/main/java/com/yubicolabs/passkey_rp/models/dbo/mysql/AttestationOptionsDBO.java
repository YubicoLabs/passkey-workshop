package com.yubicolabs.passkey_rp.models.dbo.mysql;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
public class AttestationOptionsDBO {
  @Getter
  String attestationRequest;

  @Getter
  @Setter
  Boolean isActive;

  @Getter
  String requestId;
}
