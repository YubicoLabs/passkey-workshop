package com.yubicolabs.passkey_rp.models.api;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.Valid;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.annotation.Generated;

/**
 * AssertionResultRequest
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-01-31T11:58:50.125043-06:00[America/Chicago]")
public class AssertionResultRequest {

  @JsonProperty("requestId")
  private String requestId;

  @JsonProperty("assertionResult")
  private AssertionResultRequestAssertionResult assertionResult;

  public AssertionResultRequest requestId(String requestId) {
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

  public AssertionResultRequest assertionResult(AssertionResultRequestAssertionResult assertionResult) {
    this.assertionResult = assertionResult;
    return this;
  }

  /**
   * Get assertionResult
   * 
   * @return assertionResult
   */
  @Valid
  @Schema(name = "assertionResult", required = false)
  public AssertionResultRequestAssertionResult getAssertionResult() {
    return assertionResult;
  }

  public void setAssertionResult(AssertionResultRequestAssertionResult assertionResult) {
    this.assertionResult = assertionResult;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AssertionResultRequest assertionResultRequest = (AssertionResultRequest) o;
    return Objects.equals(this.requestId, assertionResultRequest.requestId) &&
        Objects.equals(this.assertionResult, assertionResultRequest.assertionResult);
  }

  @Override
  public int hashCode() {
    return Objects.hash(requestId, assertionResult);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class AssertionResultRequest {\n");
    sb.append("    requestId: ").append(toIndentedString(requestId)).append("\n");
    sb.append("    assertionResult: ").append(toIndentedString(assertionResult)).append("\n");
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
