package com.yubicolabs.passkey_rp.models.api;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.Valid;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import javax.annotation.Generated;

/**
 * AssertionOptionsResponse
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-01-31T11:58:50.125043-06:00[America/Chicago]")
@Builder
public class AssertionOptionsResponse {

  @JsonProperty("requestId")
  private String requestId;

  @JsonProperty("publicKey")
  private AssertionOptionsResponsePublicKey publicKey;

  @JsonProperty("errorMessage")
  private String errorMessage;

  public AssertionOptionsResponse requestId(String requestId) {
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

  public AssertionOptionsResponse publicKey(AssertionOptionsResponsePublicKey publicKey) {
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
  public AssertionOptionsResponsePublicKey getPublicKey() {
    return publicKey;
  }

  public void setPublicKey(AssertionOptionsResponsePublicKey publicKey) {
    this.publicKey = publicKey;
  }

  public AssertionOptionsResponse errorMessage(String errorMessage) {
    this.errorMessage = errorMessage;
    return this;
  }

  /**
   * Get errorMessage
   * 
   * @return errorMessage
   */

  @Schema(name = "errorMessage", example = "", required = false)
  public String getErrorMessage() {
    return errorMessage;
  }

  public void setErrorMessage(String errorMessage) {
    this.errorMessage = errorMessage;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AssertionOptionsResponse assertionOptionsResponse = (AssertionOptionsResponse) o;
    return Objects.equals(this.requestId, assertionOptionsResponse.requestId) &&
        Objects.equals(this.publicKey, assertionOptionsResponse.publicKey) &&
        Objects.equals(this.errorMessage, assertionOptionsResponse.errorMessage);
  }

  @Override
  public int hashCode() {
    return Objects.hash(requestId, publicKey, errorMessage);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class AssertionOptionsResponse {\n");
    sb.append("    requestId: ").append(toIndentedString(requestId)).append("\n");
    sb.append("    publicKey: ").append(toIndentedString(publicKey)).append("\n");
    sb.append("    errorMessage: ").append(toIndentedString(errorMessage)).append("\n");
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
