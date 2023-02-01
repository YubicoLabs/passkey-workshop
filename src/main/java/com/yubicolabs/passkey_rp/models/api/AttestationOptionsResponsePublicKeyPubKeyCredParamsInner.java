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
import lombok.Builder;

import java.util.*;
import javax.annotation.Generated;

/**
 * AttestationOptionsResponsePublicKeyPubKeyCredParamsInner
 */

@JsonTypeName("AttestationOptionsResponse_publicKey_pubKeyCredParams_inner")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-01-31T11:58:50.125043-06:00[America/Chicago]")
@Builder
public class AttestationOptionsResponsePublicKeyPubKeyCredParamsInner {

  @JsonProperty("type")
  private String type;

  @JsonProperty("alg")
  private Integer alg;

  public AttestationOptionsResponsePublicKeyPubKeyCredParamsInner type(String type) {
    this.type = type;
    return this;
  }

  /**
   * Get type
   * 
   * @return type
   */

  @Schema(name = "type", example = "public-key", required = false)
  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public AttestationOptionsResponsePublicKeyPubKeyCredParamsInner alg(Integer alg) {
    this.alg = alg;
    return this;
  }

  /**
   * Get alg
   * 
   * @return alg
   */

  @Schema(name = "alg", example = "-7", required = false)
  public Integer getAlg() {
    return alg;
  }

  public void setAlg(Integer alg) {
    this.alg = alg;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AttestationOptionsResponsePublicKeyPubKeyCredParamsInner attestationOptionsResponsePublicKeyPubKeyCredParamsInner = (AttestationOptionsResponsePublicKeyPubKeyCredParamsInner) o;
    return Objects.equals(this.type, attestationOptionsResponsePublicKeyPubKeyCredParamsInner.type) &&
        Objects.equals(this.alg, attestationOptionsResponsePublicKeyPubKeyCredParamsInner.alg);
  }

  @Override
  public int hashCode() {
    return Objects.hash(type, alg);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class AttestationOptionsResponsePublicKeyPubKeyCredParamsInner {\n");
    sb.append("    type: ").append(toIndentedString(type)).append("\n");
    sb.append("    alg: ").append(toIndentedString(alg)).append("\n");
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
