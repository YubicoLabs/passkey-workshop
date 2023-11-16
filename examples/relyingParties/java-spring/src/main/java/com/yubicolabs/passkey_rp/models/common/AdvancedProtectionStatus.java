package com.yubicolabs.passkey_rp.models.common;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
public class AdvancedProtectionStatus {

  @Getter
  String userHandle;

  @Getter
  @Setter
  boolean isAdvancedProtection;

}
