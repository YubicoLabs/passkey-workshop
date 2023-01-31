package com.yubicolabs.passkey_rp.models.api;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.yubicolabs.passkey_rp.models.api.AttestationResultRequestMakeCredentialResult;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * AttestationResultRequest
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-01-31T11:58:50.125043-06:00[America/Chicago]")
public class AttestationResultRequest {

  @JsonProperty("requestId")
  private String requestId;

  @JsonProperty("makeCredentialResult")
  private AttestationResultRequestMakeCredentialResult makeCredentialResult;

  public AttestationResultRequest requestId(String requestId) {
    this.requestId = requestId;
    return this;
  }

  /**
   * Get requestId
   * @return requestId
  */
  
  @Schema(name = "requestId", example = "B-J4odOi9vcV-4TN_gpokEb1f1EI...", required = false)
  public String getRequestId() {
    return requestId;
  }

  public void setRequestId(String requestId) {
    this.requestId = requestId;
  }

  public AttestationResultRequest makeCredentialResult(AttestationResultRequestMakeCredentialResult makeCredentialResult) {
    this.makeCredentialResult = makeCredentialResult;
    return this;
  }

  /**
   * Get makeCredentialResult
   * @return makeCredentialResult
  */
  @Valid 
  @Schema(name = "makeCredentialResult", required = false)
  public AttestationResultRequestMakeCredentialResult getMakeCredentialResult() {
    return makeCredentialResult;
  }

  public void setMakeCredentialResult(AttestationResultRequestMakeCredentialResult makeCredentialResult) {
    this.makeCredentialResult = makeCredentialResult;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AttestationResultRequest attestationResultRequest = (AttestationResultRequest) o;
    return Objects.equals(this.requestId, attestationResultRequest.requestId) &&
        Objects.equals(this.makeCredentialResult, attestationResultRequest.makeCredentialResult);
  }

  @Override
  public int hashCode() {
    return Objects.hash(requestId, makeCredentialResult);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class AttestationResultRequest {\n");
    sb.append("    requestId: ").append(toIndentedString(requestId)).append("\n");
    sb.append("    makeCredentialResult: ").append(toIndentedString(makeCredentialResult)).append("\n");
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

