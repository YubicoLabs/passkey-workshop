package com.yubicolabs.passkey_rp.models.api;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import javax.annotation.Generated;

/**
 * AttestationOptionsResponsePublicKeyRp
 */

@JsonTypeName("AttestationOptionsResponse_publicKey_rp")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-01-31T11:58:50.125043-06:00[America/Chicago]")
@Builder
public class AttestationOptionsResponsePublicKeyRp {

  @JsonProperty("name")
  private String name;

  @JsonProperty("id")
  private String id;

  public AttestationOptionsResponsePublicKeyRp name(String name) {
    this.name = name;
    return this;
  }

  /**
   * Get name
   * 
   * @return name
   */

  @Schema(name = "name", example = "Example Corporation", required = false)
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public AttestationOptionsResponsePublicKeyRp id(String id) {
    this.id = id;
    return this;
  }

  /**
   * Get id
   * 
   * @return id
   */

  @Schema(name = "id", example = "login.example.com", required = false)
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
    AttestationOptionsResponsePublicKeyRp attestationOptionsResponsePublicKeyRp = (AttestationOptionsResponsePublicKeyRp) o;
    return Objects.equals(this.name, attestationOptionsResponsePublicKeyRp.name) &&
        Objects.equals(this.id, attestationOptionsResponsePublicKeyRp.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, id);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class AttestationOptionsResponsePublicKeyRp {\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
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
