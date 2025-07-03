package com.yubicolabs.keycloak.models;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CredentialResponse {

  @JsonProperty("credentials")
  List<CredentialResponseInner> credentials;

}
