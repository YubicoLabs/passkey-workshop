package com.yubicolabs.passkey_rp.models.api;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.annotation.Generated;

/**
 * AttestationResultRequestMakeCredentialResultResponse
 */

@JsonTypeName("AttestationResultRequest_makeCredentialResult_response")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-01-31T11:58:50.125043-06:00[America/Chicago]")
public class AttestationResultRequestMakeCredentialResultResponse {

  @JsonProperty("clientDataJSON")
  private String clientDataJSON;

  @JsonProperty("attestationObject")
  private String attestationObject;

  public AttestationResultRequestMakeCredentialResultResponse clientDataJSON(String clientDataJSON) {
    this.clientDataJSON = clientDataJSON;
    return this;
  }

  /**
   * Get clientDataJSON
   * 
   * @return clientDataJSON
   */

  @Schema(name = "clientDataJSON", example = "eyJj0OiJIiwidHlwZSI6IndlYmF1dGhyZWF0ZSJ9...", required = false)
  public String getClientDataJSON() {
    return clientDataJSON;
  }

  public void setClientDataJSON(String clientDataJSON) {
    this.clientDataJSON = clientDataJSON;
  }

  public AttestationResultRequestMakeCredentialResultResponse attestationObject(String attestationObject) {
    this.attestationObject = attestationObject;
    return this;
  }

  /**
   * Get attestationObject
   * 
   * @return attestationObject
   */

  @Schema(name = "attestationObject", example = "o2NmbXRNAQELBQADgbpNt2IOL4i4z96Kqo1rqSUmonen...", required = false)
  public String getAttestationObject() {
    return attestationObject;
  }

  public void setAttestationObject(String attestationObject) {
    this.attestationObject = attestationObject;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AttestationResultRequestMakeCredentialResultResponse attestationResultRequestMakeCredentialResultResponse = (AttestationResultRequestMakeCredentialResultResponse) o;
    return Objects.equals(this.clientDataJSON, attestationResultRequestMakeCredentialResultResponse.clientDataJSON) &&
        Objects.equals(this.attestationObject, attestationResultRequestMakeCredentialResultResponse.attestationObject);
  }

  @Override
  public int hashCode() {
    return Objects.hash(clientDataJSON, attestationObject);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class AttestationResultRequestMakeCredentialResultResponse {\n");
    sb.append("    clientDataJSON: ").append(toIndentedString(clientDataJSON)).append("\n");
    sb.append("    attestationObject: ").append(toIndentedString(attestationObject)).append("\n");
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
