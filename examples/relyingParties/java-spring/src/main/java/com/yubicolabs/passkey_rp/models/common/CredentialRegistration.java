package com.yubicolabs.passkey_rp.models.common;

import java.time.Instant;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.yubico.webauthn.RegisteredCredential;
import com.yubico.webauthn.data.UserIdentity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
public class CredentialRegistration {
  @Getter
  UserIdentity userIdentity;

  @Getter
  Optional<String> credentialNickname;

  @Getter
  Instant registrationTime;

  @Getter
  Instant lastUsedTime;

  @Getter
  Instant lastUpdateTime;

  @Getter
  RegisteredCredential credential;

  @Getter
  boolean isHighAssurance;

  @Getter
  @Setter
  StateEnum state;

  /**
   * Denotes an icon given by the FIDO MDS
   */
  @Getter
  Optional<String> iconURI;

  public enum StateEnum {
    ENABLED("ENABLED"),
    DISABLED("DISABLED"),
    DELETED("DELETED");

    private String value;

    StateEnum(String value) {
      this.value = value;
    }

    @JsonValue
    public String getValue() {
      return value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }

    @JsonCreator
    public static StateEnum fromValue(String value) {
      for (StateEnum b : StateEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value " + value);
    }

    public boolean stateEqual(Object o) {
      if (o instanceof StateEnum) {
        StateEnum s = (StateEnum) o;

        return s.value.equals(this.value);
      } else {
        return false;
      }
    }

  }
}
