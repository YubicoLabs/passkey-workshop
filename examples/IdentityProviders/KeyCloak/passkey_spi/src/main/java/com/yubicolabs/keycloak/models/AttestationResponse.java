package com.yubicolabs.keycloak.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Value;

@Builder
@Value
public class AttestationResponse {
  @JsonProperty
  String status;

  @JsonProperty("credential")
  AttestationResponseCredential credential;

  @JsonCreator
  public AttestationResponse(
      @JsonProperty("status") String status,
      @JsonProperty("credential") AttestationResponseCredential credential) {
    this.status = status;
    this.credential = credential;
  }
}
