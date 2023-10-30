package com.yubicolabs.passkey_rp.models.api;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.annotation.Generated;

/**
 * AttestationResultResponse
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-01-31T11:58:50.125043-06:00[America/Chicago]")
public class AttestationResultResponse {

  @JsonProperty("status")
  private String status;

  public AttestationResultResponse status(String status) {
    this.status = status;
    return this;
  }

  /**
   * Get status
   * 
   * @return status
   */

  @Schema(name = "status", example = "created", required = false)
  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  @JsonProperty("credential")
  private UserCredentialsResponseCredentialsInner credential;

  public AttestationResultResponse credential(UserCredentialsResponseCredentialsInner credential) {
    this.credential = credential;
    return this;
  }

  /**
   * Get credential
   * 
   * @return credential
   */

  @Schema(name = "credential", required = false)
  public UserCredentialsResponseCredentialsInner getCredential() {
    return credential;
  }

  public void setCredential(UserCredentialsResponseCredentialsInner credential) {
    this.credential = credential;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AttestationResultResponse attestationResultResponse = (AttestationResultResponse) o;
    return Objects.equals(this.status, attestationResultResponse.status);
  }

  @Override
  public int hashCode() {
    return Objects.hash(status);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class AttestationResultResponse {\n");
    sb.append("    status: ").append(toIndentedString(status)).append("\n");
    sb.append("    credential: ").append(toIndentedString(credential.toString())).append("\n");
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
