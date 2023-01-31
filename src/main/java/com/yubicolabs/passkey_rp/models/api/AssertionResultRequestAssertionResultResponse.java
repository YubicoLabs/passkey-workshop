package com.yubicolabs.passkey_rp.models.api;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * AssertionResultRequestAssertionResultResponse
 */

@JsonTypeName("AssertionResultRequest_assertionResult_response")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-01-31T11:58:50.125043-06:00[America/Chicago]")
public class AssertionResultRequestAssertionResultResponse {

  @JsonProperty("authenticatorData")
  private String authenticatorData;

  @JsonProperty("signature")
  private String signature;

  @JsonProperty("userHandle")
  private String userHandle;

  @JsonProperty("clientDataJSON")
  private String clientDataJSON;

  public AssertionResultRequestAssertionResultResponse authenticatorData(String authenticatorData) {
    this.authenticatorData = authenticatorData;
    return this;
  }

  /**
   * Get authenticatorData
   * @return authenticatorData
  */
  
  @Schema(name = "authenticatorData", example = "SZYN5Gh0NBcPZHZgW4_krrmihjzzuoMdl2MBAAAAAA...", required = false)
  public String getAuthenticatorData() {
    return authenticatorData;
  }

  public void setAuthenticatorData(String authenticatorData) {
    this.authenticatorData = authenticatorData;
  }

  public AssertionResultRequestAssertionResultResponse signature(String signature) {
    this.signature = signature;
    return this;
  }

  /**
   * Get signature
   * @return signature
  */
  
  @Schema(name = "signature", example = "ME8fLjd5y6TUOLWt5l9DQIhANiYig9newAJZYTzG1i5lwP", required = false)
  public String getSignature() {
    return signature;
  }

  public void setSignature(String signature) {
    this.signature = signature;
  }

  public AssertionResultRequestAssertionResultResponse userHandle(String userHandle) {
    this.userHandle = userHandle;
    return this;
  }

  /**
   * Get userHandle
   * @return userHandle
  */
  
  @Schema(name = "userHandle", example = "", required = false)
  public String getUserHandle() {
    return userHandle;
  }

  public void setUserHandle(String userHandle) {
    this.userHandle = userHandle;
  }

  public AssertionResultRequestAssertionResultResponse clientDataJSON(String clientDataJSON) {
    this.clientDataJSON = clientDataJSON;
    return this;
  }

  /**
   * Get clientDataJSON
   * @return clientDataJSON
  */
  
  @Schema(name = "clientDataJSON", example = "eyJjaGFTBrTmM4uIjoiaHR0cDovL2xvY2FsF1dGhuLmdldCJ9...", required = false)
  public String getClientDataJSON() {
    return clientDataJSON;
  }

  public void setClientDataJSON(String clientDataJSON) {
    this.clientDataJSON = clientDataJSON;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AssertionResultRequestAssertionResultResponse assertionResultRequestAssertionResultResponse = (AssertionResultRequestAssertionResultResponse) o;
    return Objects.equals(this.authenticatorData, assertionResultRequestAssertionResultResponse.authenticatorData) &&
        Objects.equals(this.signature, assertionResultRequestAssertionResultResponse.signature) &&
        Objects.equals(this.userHandle, assertionResultRequestAssertionResultResponse.userHandle) &&
        Objects.equals(this.clientDataJSON, assertionResultRequestAssertionResultResponse.clientDataJSON);
  }

  @Override
  public int hashCode() {
    return Objects.hash(authenticatorData, signature, userHandle, clientDataJSON);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class AssertionResultRequestAssertionResultResponse {\n");
    sb.append("    authenticatorData: ").append(toIndentedString(authenticatorData)).append("\n");
    sb.append("    signature: ").append(toIndentedString(signature)).append("\n");
    sb.append("    userHandle: ").append(toIndentedString(userHandle)).append("\n");
    sb.append("    clientDataJSON: ").append(toIndentedString(clientDataJSON)).append("\n");
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

