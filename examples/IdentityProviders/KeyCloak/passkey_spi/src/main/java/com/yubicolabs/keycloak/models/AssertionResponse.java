package com.yubicolabs.keycloak.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Value;

@Builder
@Value
public class AssertionResponse {

  @JsonProperty("status")
  String status;

  @JsonProperty("loa")
  int loa;

  @JsonCreator
  public AssertionResponse(@JsonProperty("status") String status, @JsonProperty("loa") int loa) {
    this.status = status;
    this.loa = loa;
  }

}
