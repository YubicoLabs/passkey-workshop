package com.yubicolabs.passkey_rp.models.api;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.yubicolabs.passkey_rp.models.api.AttestationOptionsResponsePublicKey;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.*;
import javax.annotation.Generated;

/**
 * AttestationOptionsResponse
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-01-31T11:58:50.125043-06:00[America/Chicago]")
@Builder
public class AttestationOptionsResponse {

  @JsonProperty("requestId")
  private String requestId;

  @JsonProperty("publicKey")
  private AttestationOptionsResponsePublicKey publicKey;

  public AttestationOptionsResponse requestId(String requestId) {
    this.requestId = requestId;
    return this;
  }

  /**
   * Get requestId
   * 
   * @return requestId
   */

  @Schema(name = "requestId", example = "B-J4odOi9vcV-4TN_gpokEb1f1EI...", required = false)
  public String getRequestId() {
    return requestId;
  }

  public void setRequestId(String requestId) {
    this.requestId = requestId;
  }

  public AttestationOptionsResponse publicKey(AttestationOptionsResponsePublicKey publicKey) {
    this.publicKey = publicKey;
    return this;
  }

  /**
   * Get publicKey
   * 
   * @return publicKey
   */
  @Valid
  @Schema(name = "publicKey", required = false)
  public AttestationOptionsResponsePublicKey getPublicKey() {
    return publicKey;
  }

  public void setPublicKey(AttestationOptionsResponsePublicKey publicKey) {
    this.publicKey = publicKey;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AttestationOptionsResponse attestationOptionsResponse = (AttestationOptionsResponse) o;
    return Objects.equals(this.requestId, attestationOptionsResponse.requestId) &&
        Objects.equals(this.publicKey, attestationOptionsResponse.publicKey);
  }

  @Override
  public int hashCode() {
    return Objects.hash(requestId, publicKey);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class AttestationOptionsResponse {\n");
    sb.append("    requestId: ").append(toIndentedString(requestId)).append("\n");
    sb.append("    publicKey: ").append(toIndentedString(publicKey)).append("\n");
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
