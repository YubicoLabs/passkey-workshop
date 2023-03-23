package com.yubicolabs.passkey_rp.models.api;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import javax.validation.Valid;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.annotation.Generated;

/**
 * AttestationResultRequestMakeCredentialResult
 */

@JsonTypeName("AttestationResultRequest_makeCredentialResult")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-01-31T11:58:50.125043-06:00[America/Chicago]")
public class AttestationResultRequestMakeCredentialResult {

  @JsonProperty("id")
  private String id;

  @JsonProperty("response")
  private AttestationResultRequestMakeCredentialResultResponse response;

  @JsonProperty("type")
  private String type;

  @JsonProperty("clientExtensionResults")
  private Object clientExtensionResults;

  public AttestationResultRequestMakeCredentialResult id(String id) {
    this.id = id;
    return this;
  }

  /**
   * Get id
   * 
   * @return id
   */

  @Schema(name = "id", example = "LFdoCFJSJUHc-c72yraRc_1mDvruywA", required = false)
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public AttestationResultRequestMakeCredentialResult response(
      AttestationResultRequestMakeCredentialResultResponse response) {
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
  public AttestationResultRequestMakeCredentialResultResponse getResponse() {
    return response;
  }

  public void setResponse(AttestationResultRequestMakeCredentialResultResponse response) {
    this.response = response;
  }

  public AttestationResultRequestMakeCredentialResult type(String type) {
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

  public AttestationResultRequestMakeCredentialResult clientExtensionResults(Object clientExtensionResults) {
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
    AttestationResultRequestMakeCredentialResult attestationResultRequestMakeCredentialResult = (AttestationResultRequestMakeCredentialResult) o;
    return Objects.equals(this.id, attestationResultRequestMakeCredentialResult.id) &&
        Objects.equals(this.response, attestationResultRequestMakeCredentialResult.response) &&
        Objects.equals(this.type, attestationResultRequestMakeCredentialResult.type) &&
        Objects.equals(this.clientExtensionResults,
            attestationResultRequestMakeCredentialResult.clientExtensionResults);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, response, type, clientExtensionResults);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class AttestationResultRequestMakeCredentialResult {\n");
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
