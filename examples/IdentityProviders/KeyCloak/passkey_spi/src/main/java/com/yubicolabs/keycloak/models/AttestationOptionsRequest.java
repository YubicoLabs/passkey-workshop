package com.yubicolabs.keycloak.models;

import lombok.Builder;
import lombok.Getter;

@Builder
public class AttestationOptionsRequest {

  @Getter
  private String userName;

  @Getter
  private String displayName;

  @Getter
  private AuthenticatorSelection authenticatorSelection;

}
