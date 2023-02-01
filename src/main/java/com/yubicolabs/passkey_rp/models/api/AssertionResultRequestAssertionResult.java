package com.yubicolabs.passkey_rp.models.api;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.yubicolabs.passkey_rp.models.api.AssertionResultRequestAssertionResultResponse;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.*;
import javax.annotation.Generated;

/**
 * AssertionResultRequestAssertionResult
 */

@JsonTypeName("AssertionResultRequest_assertionResult")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-02-01T09:16:11.663223-06:00[America/Chicago]")
public class AssertionResultRequestAssertionResult {

  @JsonProperty("id")
  private String id;

  @JsonProperty("response")
  private AssertionResultRequestAssertionResultResponse response;

  @JsonProperty("type")
  private String type = "public-key";

  @JsonProperty("clientExtensionResults")
  private Object clientExtensionResults;

  public AssertionResultRequestAssertionResult id(String id) {
    this.id = id;
    return this;
  }

  /**
   * Get id
   * 
   * @return id
   */

  @Schema(name = "id", example = "LFdoCFJTyB82ZzSJUHc-c72yraRc_1mPvG", required = false)
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public AssertionResultRequestAssertionResult response(AssertionResultRequestAssertionResultResponse response) {
    this.response = response;
    return this;
  }

  /**
   * Get response
   * 
   * @return response
   */
  @Valid
  @Schema(name = "response", required = false)
  public AssertionResultRequestAssertionResultResponse getResponse() {
    return response;
  }

  public void setResponse(AssertionResultRequestAssertionResultResponse response) {
    this.response = response;
  }

  public AssertionResultRequestAssertionResult type(String type) {
    this.type = type;
    return this;
  }

  /**
   * Get type
   * 
   * @return type
   */

  @Schema(name = "type", example = "public-key", required = false)
  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public AssertionResultRequestAssertionResult clientExtensionResults(Object clientExtensionResults) {
    this.clientExtensionResults = clientExtensionResults;
    return this;
  }

  /**
   * Get clientExtensionResults
   * 
   * @return clientExtensionResults
   */

  @Schema(name = "clientExtensionResults", example = "{}", required = false)
  public Object getClientExtensionResults() {
    return clientExtensionResults;
  }

  public void setClientExtensionResults(Object clientExtensionResults) {
    this.clientExtensionResults = clientExtensionResults;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AssertionResultRequestAssertionResult assertionResultRequestAssertionResult = (AssertionResultRequestAssertionResult) o;
    return Objects.equals(this.id, assertionResultRequestAssertionResult.id) &&
        Objects.equals(this.response, assertionResultRequestAssertionResult.response) &&
        Objects.equals(this.type, assertionResultRequestAssertionResult.type) &&
        Objects.equals(this.clientExtensionResults, assertionResultRequestAssertionResult.clientExtensionResults);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, response, type, clientExtensionResults);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class AssertionResultRequestAssertionResult {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    response: ").append(toIndentedString(response)).append("\n");
    sb.append("    type: ").append(toIndentedString(type)).append("\n");
    sb.append("    clientExtensionResults: ").append(toIndentedString(clientExtensionResults)).append("\n");
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
