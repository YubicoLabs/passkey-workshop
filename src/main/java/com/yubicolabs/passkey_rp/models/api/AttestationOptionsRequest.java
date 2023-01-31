package com.yubicolabs.passkey_rp.models.api;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.yubicolabs.passkey_rp.models.api.AttestationOptionsRequestAuthenticatorSelection;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * AttestationOptionsRequest
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-01-31T11:58:50.125043-06:00[America/Chicago]")
public class AttestationOptionsRequest {

  @JsonProperty("userName")
  private String userName;

  @JsonProperty("displayName")
  private String displayName;

  @JsonProperty("authenticatorSelection")
  private AttestationOptionsRequestAuthenticatorSelection authenticatorSelection;

  /**
   * Gets or Sets attestation
   */
  public enum AttestationEnum {
    DIRECT("direct"),
    
    ENTERPRISE("enterprise"),
    
    INDIRECT("indirect"),
    
    NONE("none");

    private String value;

    AttestationEnum(String value) {
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
    public static AttestationEnum fromValue(String value) {
      for (AttestationEnum b : AttestationEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
  }

  @JsonProperty("attestation")
  private AttestationEnum attestation;

  public AttestationOptionsRequest userName(String userName) {
    this.userName = userName;
    return this;
  }

  /**
   * Get userName
   * @return userName
  */
  
  @Schema(name = "userName", example = "janedoe@example.com", required = false)
  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  public AttestationOptionsRequest displayName(String displayName) {
    this.displayName = displayName;
    return this;
  }

  /**
   * Get displayName
   * @return displayName
  */
  
  @Schema(name = "displayName", example = "John Doe", required = false)
  public String getDisplayName() {
    return displayName;
  }

  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }

  public AttestationOptionsRequest authenticatorSelection(AttestationOptionsRequestAuthenticatorSelection authenticatorSelection) {
    this.authenticatorSelection = authenticatorSelection;
    return this;
  }

  /**
   * Get authenticatorSelection
   * @return authenticatorSelection
  */
  @Valid 
  @Schema(name = "authenticatorSelection", required = false)
  public AttestationOptionsRequestAuthenticatorSelection getAuthenticatorSelection() {
    return authenticatorSelection;
  }

  public void setAuthenticatorSelection(AttestationOptionsRequestAuthenticatorSelection authenticatorSelection) {
    this.authenticatorSelection = authenticatorSelection;
  }

  public AttestationOptionsRequest attestation(AttestationEnum attestation) {
    this.attestation = attestation;
    return this;
  }

  /**
   * Get attestation
   * @return attestation
  */
  
  @Schema(name = "attestation", example = "direct", required = false)
  public AttestationEnum getAttestation() {
    return attestation;
  }

  public void setAttestation(AttestationEnum attestation) {
    this.attestation = attestation;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AttestationOptionsRequest attestationOptionsRequest = (AttestationOptionsRequest) o;
    return Objects.equals(this.userName, attestationOptionsRequest.userName) &&
        Objects.equals(this.displayName, attestationOptionsRequest.displayName) &&
        Objects.equals(this.authenticatorSelection, attestationOptionsRequest.authenticatorSelection) &&
        Objects.equals(this.attestation, attestationOptionsRequest.attestation);
  }

  @Override
  public int hashCode() {
    return Objects.hash(userName, displayName, authenticatorSelection, attestation);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class AttestationOptionsRequest {\n");
    sb.append("    userName: ").append(toIndentedString(userName)).append("\n");
    sb.append("    displayName: ").append(toIndentedString(displayName)).append("\n");
    sb.append("    authenticatorSelection: ").append(toIndentedString(authenticatorSelection)).append("\n");
    sb.append("    attestation: ").append(toIndentedString(attestation)).append("\n");
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

