package com.yubicolabs.passkey_rp.models.api;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.ArrayList;
import java.util.List;
import javax.validation.Valid;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import javax.annotation.Generated;

/**
 * AttestationOptionsResponsePublicKey
 */

@JsonTypeName("AttestationOptionsResponse_publicKey")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-01-31T11:58:50.125043-06:00[America/Chicago]")
@Builder
public class AttestationOptionsResponsePublicKey {

  @JsonProperty("rp")
  private AttestationOptionsResponsePublicKeyRp rp;

  @JsonProperty("user")
  private AttestationOptionsResponsePublicKeyUser user;

  @JsonProperty("challenge")
  private String challenge;

  @JsonProperty("pubKeyCredParams")
  @Valid
  private List<AttestationOptionsResponsePublicKeyPubKeyCredParamsInner> pubKeyCredParams = null;

  @JsonProperty("timeout")
  private Integer timeout;

  @JsonProperty("excludeCredentials")
  @Valid
  private List<AttestationOptionsResponsePublicKeyExcludeCredentialsInner> excludeCredentials = null;

  @JsonProperty("authenticatorSelection")
  private AttestationOptionsResponsePublicKeyAuthenticatorSelection authenticatorSelection;

  /**
   * Gets or Sets attestation
   */
  public enum AttestationEnum {
    NONE("none"),

    INDIRECT("indirect"),

    DIRECT("direct");

    private String value;

    AttestationEnum(String value) {
      this.value = value;
    }

    @JsonValue
    public String getValue() {
      return value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }

    @JsonCreator
    public static AttestationEnum fromValue(String value) {
      for (AttestationEnum b : AttestationEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
  }

  @JsonProperty("attestation")
  private AttestationEnum attestation = AttestationEnum.DIRECT;

  public AttestationOptionsResponsePublicKey rp(AttestationOptionsResponsePublicKeyRp rp) {
    this.rp = rp;
    return this;
  }

  /**
   * Get rp
   * 
   * @return rp
   */
  @Valid
  @Schema(name = "rp", required = false)
  public AttestationOptionsResponsePublicKeyRp getRp() {
    return rp;
  }

  public void setRp(AttestationOptionsResponsePublicKeyRp rp) {
    this.rp = rp;
  }

  public AttestationOptionsResponsePublicKey user(AttestationOptionsResponsePublicKeyUser user) {
    this.user = user;
    return this;
  }

  /**
   * Get user
   * 
   * @return user
   */
  @Valid
  @Schema(name = "user", required = false)
  public AttestationOptionsResponsePublicKeyUser getUser() {
    return user;
  }

  public void setUser(AttestationOptionsResponsePublicKeyUser user) {
    this.user = user;
  }

  public AttestationOptionsResponsePublicKey challenge(String challenge) {
    this.challenge = challenge;
    return this;
  }

  /**
   * Get challenge
   * 
   * @return challenge
   */

  @Schema(name = "challenge", example = "uhUjPNlZfvn7onwuhNdsLPkkE5Fv-lUN", required = false)
  public String getChallenge() {
    return challenge;
  }

  public void setChallenge(String challenge) {
    this.challenge = challenge;
  }

  public AttestationOptionsResponsePublicKey pubKeyCredParams(
      List<AttestationOptionsResponsePublicKeyPubKeyCredParamsInner> pubKeyCredParams) {
    this.pubKeyCredParams = pubKeyCredParams;
    return this;
  }

  public AttestationOptionsResponsePublicKey addPubKeyCredParamsItem(
      AttestationOptionsResponsePublicKeyPubKeyCredParamsInner pubKeyCredParamsItem) {
    if (this.pubKeyCredParams == null) {
      this.pubKeyCredParams = new ArrayList<>();
    }
    this.pubKeyCredParams.add(pubKeyCredParamsItem);
    return this;
  }

  /**
   * Get pubKeyCredParams
   * 
   * @return pubKeyCredParams
   */
  @Valid
  @Schema(name = "pubKeyCredParams", required = false)
  public List<AttestationOptionsResponsePublicKeyPubKeyCredParamsInner> getPubKeyCredParams() {
    return pubKeyCredParams;
  }

  public void setPubKeyCredParams(List<AttestationOptionsResponsePublicKeyPubKeyCredParamsInner> pubKeyCredParams) {
    this.pubKeyCredParams = pubKeyCredParams;
  }

  public AttestationOptionsResponsePublicKey timeout(Integer timeout) {
    this.timeout = timeout;
    return this;
  }

  /**
   * Get timeout
   * 
   * @return timeout
   */

  @Schema(name = "timeout", example = "10000", required = false)
  public Integer getTimeout() {
    return timeout;
  }

  public void setTimeout(Integer timeout) {
    this.timeout = timeout;
  }

  public AttestationOptionsResponsePublicKey excludeCredentials(
      List<AttestationOptionsResponsePublicKeyExcludeCredentialsInner> excludeCredentials) {
    this.excludeCredentials = excludeCredentials;
    return this;
  }

  public AttestationOptionsResponsePublicKey addExcludeCredentialsItem(
      AttestationOptionsResponsePublicKeyExcludeCredentialsInner excludeCredentialsItem) {
    if (this.excludeCredentials == null) {
      this.excludeCredentials = new ArrayList<>();
    }
    this.excludeCredentials.add(excludeCredentialsItem);
    return this;
  }

  /**
   * Get excludeCredentials
   * 
   * @return excludeCredentials
   */
  @Valid
  @Schema(name = "excludeCredentials", required = false)
  public List<AttestationOptionsResponsePublicKeyExcludeCredentialsInner> getExcludeCredentials() {
    return excludeCredentials;
  }

  public void setExcludeCredentials(
      List<AttestationOptionsResponsePublicKeyExcludeCredentialsInner> excludeCredentials) {
    this.excludeCredentials = excludeCredentials;
  }

  public AttestationOptionsResponsePublicKey authenticatorSelection(
      AttestationOptionsResponsePublicKeyAuthenticatorSelection authenticatorSelection) {
    this.authenticatorSelection = authenticatorSelection;
    return this;
  }

  /**
   * Get authenticatorSelection
   * 
   * @return authenticatorSelection
   */
  @Valid
  @Schema(name = "authenticatorSelection", required = false)
  public AttestationOptionsResponsePublicKeyAuthenticatorSelection getAuthenticatorSelection() {
    return authenticatorSelection;
  }

  public void setAuthenticatorSelection(
      AttestationOptionsResponsePublicKeyAuthenticatorSelection authenticatorSelection) {
    this.authenticatorSelection = authenticatorSelection;
  }

  public AttestationOptionsResponsePublicKey attestation(AttestationEnum attestation) {
    this.attestation = attestation;
    return this;
  }

  /**
   * Get attestation
   * 
   * @return attestation
   */

  @Schema(name = "attestation", example = "direct", required = false)
  public AttestationEnum getAttestation() {
    return attestation;
  }

  public void setAttestation(AttestationEnum attestation) {
    this.attestation = attestation;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AttestationOptionsResponsePublicKey attestationOptionsResponsePublicKey = (AttestationOptionsResponsePublicKey) o;
    return Objects.equals(this.rp, attestationOptionsResponsePublicKey.rp) &&
        Objects.equals(this.user, attestationOptionsResponsePublicKey.user) &&
        Objects.equals(this.challenge, attestationOptionsResponsePublicKey.challenge) &&
        Objects.equals(this.pubKeyCredParams, attestationOptionsResponsePublicKey.pubKeyCredParams) &&
        Objects.equals(this.timeout, attestationOptionsResponsePublicKey.timeout) &&
        Objects.equals(this.excludeCredentials, attestationOptionsResponsePublicKey.excludeCredentials) &&
        Objects.equals(this.authenticatorSelection, attestationOptionsResponsePublicKey.authenticatorSelection) &&
        Objects.equals(this.attestation, attestationOptionsResponsePublicKey.attestation);
  }

  @Override
  public int hashCode() {
    return Objects.hash(rp, user, challenge, pubKeyCredParams, timeout, excludeCredentials, authenticatorSelection,
        attestation);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class AttestationOptionsResponsePublicKey {\n");
    sb.append("    rp: ").append(toIndentedString(rp)).append("\n");
    sb.append("    user: ").append(toIndentedString(user)).append("\n");
    sb.append("    challenge: ").append(toIndentedString(challenge)).append("\n");
    sb.append("    pubKeyCredParams: ").append(toIndentedString(pubKeyCredParams)).append("\n");
    sb.append("    timeout: ").append(toIndentedString(timeout)).append("\n");
    sb.append("    excludeCredentials: ").append(toIndentedString(excludeCredentials)).append("\n");
    sb.append("    authenticatorSelection: ").append(toIndentedString(authenticatorSelection)).append("\n");
    sb.append("    attestation: ").append(toIndentedString(attestation)).append("\n");
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
