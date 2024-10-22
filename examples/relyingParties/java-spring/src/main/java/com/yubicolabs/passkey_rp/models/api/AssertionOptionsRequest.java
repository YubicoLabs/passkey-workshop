package com.yubicolabs.passkey_rp.models.api;

import java.util.Objects;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.annotation.Generated;

/**
 * AssertionOptionsRequest
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-01-31T11:58:50.125043-06:00[America/Chicago]")
public class AssertionOptionsRequest {

  @JsonProperty("userName")
  private String userName;

  @JsonProperty("hints")
  private Optional<String[]> hints = Optional.ofNullable(null);

  public AssertionOptionsRequest userName(String userName) {
    this.userName = userName;
    return this;
  }

  /**
   * Get userName
   * 
   * @return userName
   */

  @Schema(name = "userName", example = "janedoe@example.com", required = false)
  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  public void setHints(String[] hints) {
    this.hints = Optional.ofNullable(hints);
  }

  @Schema(name = "hints", example = "[\"security-keys\", \"client-device\"]", required = false)
  public Optional<String[]> getHints() {
    return hints;
  }

  // TODO - Currently not checking equality of hints
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AssertionOptionsRequest assertionOptionsRequest = (AssertionOptionsRequest) o;
    return Objects.equals(this.userName, assertionOptionsRequest.userName);
  }

  @Override
  public int hashCode() {
    return Objects.hash(userName);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class AssertionOptionsRequest {\n");
    sb.append("    userName: ").append(toIndentedString(userName)).append("\n");
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
