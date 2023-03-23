package com.yubicolabs.PasskeyAuthenticator;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;

@Getter
public class AssertionResponse {

  @JsonProperty("username")
  String username;

  @JsonProperty("assertionResponse")
  String assertionResponse;

  @JsonCreator
  public AssertionResponse(@JsonProperty("username") String username,
      @JsonProperty("assertionResponse") String assertionResponse) {
    this.username = username;
    this.assertionResponse = assertionResponse;
  }

}
