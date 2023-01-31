package com.yubicolabs.passkey_rp.models.api;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.yubicolabs.passkey_rp.models.api.UserCredentialsResponseCredentialsInner;
import java.util.ArrayList;
import java.util.List;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * UserCredentialsResponse
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-01-31T11:58:50.125043-06:00[America/Chicago]")
public class UserCredentialsResponse {

  @JsonProperty("credentials")
  @Valid
  private List<UserCredentialsResponseCredentialsInner> credentials = null;

  public UserCredentialsResponse credentials(List<UserCredentialsResponseCredentialsInner> credentials) {
    this.credentials = credentials;
    return this;
  }

  public UserCredentialsResponse addCredentialsItem(UserCredentialsResponseCredentialsInner credentialsItem) {
    if (this.credentials == null) {
      this.credentials = new ArrayList<>();
    }
    this.credentials.add(credentialsItem);
    return this;
  }

  /**
   * Get credentials
   * @return credentials
  */
  @Valid 
  @Schema(name = "credentials", required = false)
  public List<UserCredentialsResponseCredentialsInner> getCredentials() {
    return credentials;
  }

  public void setCredentials(List<UserCredentialsResponseCredentialsInner> credentials) {
    this.credentials = credentials;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    UserCredentialsResponse userCredentialsResponse = (UserCredentialsResponse) o;
    return Objects.equals(this.credentials, userCredentialsResponse.credentials);
  }

  @Override
  public int hashCode() {
    return Objects.hash(credentials);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class UserCredentialsResponse {\n");
    sb.append("    credentials: ").append(toIndentedString(credentials)).append("\n");
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

