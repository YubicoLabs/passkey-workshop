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
 * AttestationOptionsResponsePublicKeyExcludeCredentialsInner
 */

@JsonTypeName("AttestationOptionsResponse_publicKey_excludeCredentials_inner")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-01-31T11:58:50.125043-06:00[America/Chicago]")
public class AttestationOptionsResponsePublicKeyExcludeCredentialsInner {

  @JsonProperty("type")
  private String type;

  @JsonProperty("id")
  private String id;

  public AttestationOptionsResponsePublicKeyExcludeCredentialsInner type(String type) {
    this.type = type;
    return this;
  }

  /**
   * Get type
   * @return type
  */
  
  @Schema(name = "type", example = "public-key", required = false)
  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public AttestationOptionsResponsePublicKeyExcludeCredentialsInner id(String id) {
    this.id = id;
    return this;
  }

  /**
   * Get id
   * @return id
  */
  
  @Schema(name = "id", example = "opQf1WmYAa5au-kk-pUKJIQp...", required = false)
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AttestationOptionsResponsePublicKeyExcludeCredentialsInner attestationOptionsResponsePublicKeyExcludeCredentialsInner = (AttestationOptionsResponsePublicKeyExcludeCredentialsInner) o;
    return Objects.equals(this.type, attestationOptionsResponsePublicKeyExcludeCredentialsInner.type) &&
        Objects.equals(this.id, attestationOptionsResponsePublicKeyExcludeCredentialsInner.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(type, id);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class AttestationOptionsResponsePublicKeyExcludeCredentialsInner {\n");
    sb.append("    type: ").append(toIndentedString(type)).append("\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
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

