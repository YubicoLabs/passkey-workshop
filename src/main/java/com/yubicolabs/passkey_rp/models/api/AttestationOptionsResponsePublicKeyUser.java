package com.yubicolabs.passkey_rp.models.api;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import javax.annotation.Generated;

/**
 * AttestationOptionsResponsePublicKeyUser
 */

@JsonTypeName("AttestationOptionsResponse_publicKey_user")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-01-31T11:58:50.125043-06:00[America/Chicago]")
@Builder
public class AttestationOptionsResponsePublicKeyUser {

  @JsonProperty("id")
  private String id;

  @JsonProperty("name")
  private String name;

  @JsonProperty("displayName")
  private String displayName;

  public AttestationOptionsResponsePublicKeyUser id(String id) {
    this.id = id;
    return this;
  }

  /**
   * Get id
   * 
   * @return id
   */

  @Schema(name = "id", example = "4542432", required = false)
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public AttestationOptionsResponsePublicKeyUser name(String name) {
    this.name = name;
    return this;
  }

  /**
   * Get name
   * 
   * @return name
   */

  @Schema(name = "name", example = "janedoe@example.com", required = false)
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public AttestationOptionsResponsePublicKeyUser displayName(String displayName) {
    this.displayName = displayName;
    return this;
  }

  /**
   * Get displayName
   * 
   * @return displayName
   */

  @Schema(name = "displayName", example = "Jane Doe", required = false)
  public String getDisplayName() {
    return displayName;
  }

  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AttestationOptionsResponsePublicKeyUser attestationOptionsResponsePublicKeyUser = (AttestationOptionsResponsePublicKeyUser) o;
    return Objects.equals(this.id, attestationOptionsResponsePublicKeyUser.id) &&
        Objects.equals(this.name, attestationOptionsResponsePublicKeyUser.name) &&
        Objects.equals(this.displayName, attestationOptionsResponsePublicKeyUser.displayName);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, name, displayName);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class AttestationOptionsResponsePublicKeyUser {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    displayName: ").append(toIndentedString(displayName)).append("\n");
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
