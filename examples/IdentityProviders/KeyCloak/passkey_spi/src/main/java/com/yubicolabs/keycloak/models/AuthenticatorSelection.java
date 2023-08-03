package com.yubicolabs.keycloak.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.Builder;
import lombok.Getter;

@Builder
public class AuthenticatorSelection {
  public enum ResidentKeyEnum {
    PREFERRED("preferred"),

    DISCOURAGED("discouraged"),

    REQUIRED("required"),

    EMPTY("");

    private String value;

    ResidentKeyEnum(String value) {
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
    public static ResidentKeyEnum fromValue(String value) {
      for (ResidentKeyEnum b : ResidentKeyEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
  }

  @Getter
  private ResidentKeyEnum residentKey;

  /**
   * Gets or Sets authenticatorAttachment
   */
  public enum AuthenticatorAttachmentEnum {
    CROSS_PLATFORM("cross-platform"),

    PLATFORM("platform"),

    NONE("none"),

    EMPTY("");

    private String value;

    AuthenticatorAttachmentEnum(String value) {
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
    public static AuthenticatorAttachmentEnum fromValue(String value) {
      for (AuthenticatorAttachmentEnum b : AuthenticatorAttachmentEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
  }

  @Getter
  private AuthenticatorAttachmentEnum authenticatorAttachment;

  public enum UserVerificationEnum {
    PREFERRED("preferred"),

    DISCOURAGED("discouraged"),

    REQUIRED("required"),

    EMPTY("");

    private String value;

    UserVerificationEnum(String value) {
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
    public static UserVerificationEnum fromValue(String value) {
      for (UserVerificationEnum b : UserVerificationEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
  }

  @Getter
  private UserVerificationEnum userVerification;

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class AttestationOptionsRequestAuthenticatorSelection {\n");
    sb.append("    residentKey: ").append(toIndentedString(residentKey)).append("\n");
    sb.append("    authenticatorAttachment: ").append(toIndentedString(authenticatorAttachment)).append("\n");
    sb.append("    userVerification: ").append(toIndentedString(userVerification)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}
