package com.yubicolabs.passkey_rp.models.api;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.annotation.Generated;

/**
 * UserCredentialUpdate
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-01-31T11:58:50.125043-06:00[America/Chicago]")
public class UserCredentialUpdate {

  @JsonProperty("id")
  private String id;

  @JsonProperty("nickName")
  private String nickName;

  public UserCredentialUpdate id(String id) {
    this.id = id;
    return this;
  }

  /**
   * Get id
   * 
   * @return id
   */

  @Schema(name = "id", example = "DthUeofXNtlMevkt_M7aiD3cm70...", required = false)
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public UserCredentialUpdate nickName(String nickName) {
    this.nickName = nickName;
    return this;
  }

  /**
   * Get nickName
   * 
   * @return nickName
   */

  @Schema(name = "nickName", example = "YubiKey 5Ci home", required = false)
  public String getNickName() {
    return nickName;
  }

  public void setNickName(String nickName) {
    this.nickName = nickName;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    UserCredentialUpdate userCredentialUpdate = (UserCredentialUpdate) o;
    return Objects.equals(this.id, userCredentialUpdate.id) &&
        Objects.equals(this.nickName, userCredentialUpdate.nickName);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, nickName);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class UserCredentialUpdate {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    nickName: ").append(toIndentedString(nickName)).append("\n");
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
