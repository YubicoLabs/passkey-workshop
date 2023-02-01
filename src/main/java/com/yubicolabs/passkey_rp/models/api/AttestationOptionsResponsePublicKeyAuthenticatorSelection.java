package com.yubicolabs.passkey_rp.models.api;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonValue;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.*;
import javax.annotation.Generated;

/**
 * AttestationOptionsResponsePublicKeyAuthenticatorSelection
 */

@JsonTypeName("AttestationOptionsResponse_publicKey_authenticatorSelection")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-01-31T11:58:50.125043-06:00[America/Chicago]")
@Builder
@JsonInclude(Include.NON_NULL)
public class AttestationOptionsResponsePublicKeyAuthenticatorSelection {

  /**
   * Gets or Sets residentKey
   */
  public enum ResidentKeyEnum {
    DISCOURAGED("discouraged"),

    PREFERRED("preferred"),

    REQUIRED("required");

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

  @JsonProperty("residentKey")
  private ResidentKeyEnum residentKey = ResidentKeyEnum.PREFERRED;

  /**
   * Gets or Sets authenticatorAttachment
   */
  public enum AuthenticatorAttachmentEnum {
    CROSS_PLATFORM("cross-platform"),

    PLATFORM("platform");

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

  @JsonProperty("authenticatorAttachment")
  private AuthenticatorAttachmentEnum authenticatorAttachment = AuthenticatorAttachmentEnum.CROSS_PLATFORM;

  /**
   * Gets or Sets userVerification
   */
  public enum UserVerificationEnum {
    REQUIRED("required"),

    PREFERRED("preferred"),

    DISCOURAGED("discouraged");

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

  @JsonProperty("userVerification")
  private UserVerificationEnum userVerification = UserVerificationEnum.PREFERRED;

  public AttestationOptionsResponsePublicKeyAuthenticatorSelection residentKey(ResidentKeyEnum residentKey) {
    this.residentKey = residentKey;
    return this;
  }

  /**
   * Get residentKey
   * 
   * @return residentKey
   */

  @Schema(name = "residentKey", example = "preferred", required = false)
  public ResidentKeyEnum getResidentKey() {
    return residentKey;
  }

  public void setResidentKey(ResidentKeyEnum residentKey) {
    this.residentKey = residentKey;
  }

  public AttestationOptionsResponsePublicKeyAuthenticatorSelection authenticatorAttachment(
      AuthenticatorAttachmentEnum authenticatorAttachment) {
    this.authenticatorAttachment = authenticatorAttachment;
    return this;
  }

  /**
   * Get authenticatorAttachment
   * 
   * @return authenticatorAttachment
   */

  @Schema(name = "authenticatorAttachment", example = "cross-platform", required = false)
  public AuthenticatorAttachmentEnum getAuthenticatorAttachment() {
    return authenticatorAttachment;
  }

  public void setAuthenticatorAttachment(AuthenticatorAttachmentEnum authenticatorAttachment) {
    this.authenticatorAttachment = authenticatorAttachment;
  }

  public AttestationOptionsResponsePublicKeyAuthenticatorSelection userVerification(
      UserVerificationEnum userVerification) {
    this.userVerification = userVerification;
    return this;
  }

  /**
   * Get userVerification
   * 
   * @return userVerification
   */

  @Schema(name = "userVerification", example = "preferred", required = false)
  public UserVerificationEnum getUserVerification() {
    return userVerification;
  }

  public void setUserVerification(UserVerificationEnum userVerification) {
    this.userVerification = userVerification;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AttestationOptionsResponsePublicKeyAuthenticatorSelection attestationOptionsResponsePublicKeyAuthenticatorSelection = (AttestationOptionsResponsePublicKeyAuthenticatorSelection) o;
    return Objects.equals(this.residentKey, attestationOptionsResponsePublicKeyAuthenticatorSelection.residentKey) &&
        Objects.equals(this.authenticatorAttachment,
            attestationOptionsResponsePublicKeyAuthenticatorSelection.authenticatorAttachment)
        &&
        Objects.equals(this.userVerification,
            attestationOptionsResponsePublicKeyAuthenticatorSelection.userVerification);
  }

  @Override
  public int hashCode() {
    return Objects.hash(residentKey, authenticatorAttachment, userVerification);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class AttestationOptionsResponsePublicKeyAuthenticatorSelection {\n");
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
