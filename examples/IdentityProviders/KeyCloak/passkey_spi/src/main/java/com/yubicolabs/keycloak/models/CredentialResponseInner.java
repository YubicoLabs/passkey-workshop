package com.yubicolabs.keycloak.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CredentialResponseInner {

  @JsonProperty("id")
  private String id;

  @JsonProperty("type")
  private String type;

  @JsonProperty("nickName")
  private String nickName;

  @JsonProperty("registrationTime")
  private long registrationTime;

  @JsonProperty("lastUsedTime")
  private long lastUsedTime;

  @JsonProperty("iconURI")
  private String iconURI;

  @JsonProperty("isHighAssurance")
  private boolean isHighAssurance;

  @JsonProperty("state")
  private String state;

}
