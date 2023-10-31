package com.yubicolabs.bank_app.models.api;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * CreateAccountRequest
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-06-30T13:35:17.173429-05:00[America/Chicago]")
public class CreateAccountRequest {

  @JsonProperty("userHandle")
  private String userHandle;

  public CreateAccountRequest userHandle(String userHandle) {
    this.userHandle = userHandle;
    return this;
  }

  /**
   * Get userHandle
   * @return userHandle
  */
  
  @Schema(name = "userHandle", example = "_Z07XVOop2ZcmWB0lpH_FHochR0", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  public String getUserHandle() {
    return userHandle;
  }

  public void setUserHandle(String userHandle) {
    this.userHandle = userHandle;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CreateAccountRequest createAccountRequest = (CreateAccountRequest) o;
    return Objects.equals(this.userHandle, createAccountRequest.userHandle);
  }

  @Override
  public int hashCode() {
    return Objects.hash(userHandle);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CreateAccountRequest {\n");
    sb.append("    userHandle: ").append(toIndentedString(userHandle)).append("\n");
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

