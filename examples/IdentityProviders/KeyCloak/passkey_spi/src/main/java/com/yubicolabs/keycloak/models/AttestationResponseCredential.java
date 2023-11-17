package com.yubicolabs.keycloak.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class AttestationResponseCredential {
  @JsonProperty("id")
  String id;

  @JsonProperty("type")
  String type;

  @JsonProperty("nickName")
  String nickName;

  @JsonProperty("registrationTime")
  String registrationTime;

  @JsonProperty("lastUsedTime")
  String lastUsedTime;

  @JsonProperty("iconURI")
  String iconURI;

  @JsonProperty("isHighAssurance")
  boolean isHighAssurance;

  @JsonProperty("state")
  String state;

  @JsonCreator
  public AttestationResponseCredential(@JsonProperty("id") String id, @JsonProperty("type") String type,
      @JsonProperty("nickName") String nickName, @JsonProperty("registrationTime") String registrationTime,
      @JsonProperty("lastUsedTime") String lastUsedTime, @JsonProperty("iconURI") String iconURI,
      @JsonProperty("isHighAssurance") boolean isHighAssurance, @JsonProperty("state") String state) {
    this.id = id;
    this.type = type;
    this.nickName = nickName;
    this.registrationTime = registrationTime;
    this.lastUsedTime = lastUsedTime;
    this.iconURI = iconURI;
    this.isHighAssurance = isHighAssurance;
    this.state = state;
  }

}
