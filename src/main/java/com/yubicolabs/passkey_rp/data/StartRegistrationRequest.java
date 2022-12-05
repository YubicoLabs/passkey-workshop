package com.yubicolabs.passkey_rp.data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Value;

@Value
public class StartRegistrationRequest {
  String username;
  String uid;

  @JsonCreator
  public StartRegistrationRequest(
      @JsonProperty("username") String username,
      @JsonProperty("uid") String uid) {
    this.username = username;
    this.uid = uid;
  }
}
