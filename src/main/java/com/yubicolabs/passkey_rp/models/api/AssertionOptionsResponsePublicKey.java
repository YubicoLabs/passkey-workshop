package com.yubicolabs.passkey_rp.models.api;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.ArrayList;
import java.util.List;
import javax.validation.Valid;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import javax.annotation.Generated;

/**
 * AssertionOptionsResponsePublicKey
 */

@JsonTypeName("AssertionOptionsResponse_publicKey")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-01-31T11:58:50.125043-06:00[America/Chicago]")
@Builder
@JsonInclude(Include.NON_NULL)
public class AssertionOptionsResponsePublicKey {

  @JsonProperty("challenge")
  private String challenge;

  @JsonProperty("timeout")
  private Integer timeout;

  @JsonProperty("rpId")
  private String rpId;

  @JsonProperty("allowCredentials")
  @Valid
  private List<AssertionOptionsResponsePublicKeyAllowCredentialsInner> allowCredentials = null;

  /**
   * Gets or Sets userVerification
   */
  public enum UserVerificationEnum {
    REQUIRED("required"),

    PREFERRED("preferred"),

    DISCOURAGED("discouraged");

    private String value;

    UserVerificationEnum(String value) {
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
    public static UserVerificationEnum fromValue(String value) {
      for (UserVerificationEnum b : UserVerificationEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
  }

  @JsonProperty("userVerification")
  private UserVerificationEnum userVerification = UserVerificationEnum.PREFERRED;

  public AssertionOptionsResponsePublicKey challenge(String challenge) {
    this.challenge = challenge;
    return this;
  }

  /**
   * Get challenge
   * 
   * @return challenge
   */

  @Schema(name = "challenge", example = "m7xl_TkTcCe0WcXI2M-4ro9vJAuwcj4m", required = false)
  public String getChallenge() {
    return challenge;
  }

  public void setChallenge(String challenge) {
    this.challenge = challenge;
  }

  public AssertionOptionsResponsePublicKey timeout(Integer timeout) {
    this.timeout = timeout;
    return this;
  }

  /**
   * Get timeout
   * 
   * @return timeout
   */

  @Schema(name = "timeout", example = "20000", required = false)
  public Integer getTimeout() {
    return timeout;
  }

  public void setTimeout(Integer timeout) {
    this.timeout = timeout;
  }

  public AssertionOptionsResponsePublicKey rpId(String rpId) {
    this.rpId = rpId;
    return this;
  }

  /**
   * Get rpId
   * 
   * @return rpId
   */

  @Schema(name = "rpId", example = "example.com", required = false)
  public String getRpId() {
    return rpId;
  }

  public void setRpId(String rpId) {
    this.rpId = rpId;
  }

  public AssertionOptionsResponsePublicKey allowCredentials(
      List<AssertionOptionsResponsePublicKeyAllowCredentialsInner> allowCredentials) {
    this.allowCredentials = allowCredentials;
    return this;
  }

  public AssertionOptionsResponsePublicKey addAllowCredentialsItem(
      AssertionOptionsResponsePublicKeyAllowCredentialsInner allowCredentialsItem) {
    if (this.allowCredentials == null) {
      this.allowCredentials = new ArrayList<>();
    }
    this.allowCredentials.add(allowCredentialsItem);
    return this;
  }

  /**
   * Get allowCredentials
   * 
   * @return allowCredentials
   */
  @Valid
  @Schema(name = "allowCredentials", required = false)
  public List<AssertionOptionsResponsePublicKeyAllowCredentialsInner> getAllowCredentials() {
    return allowCredentials;
  }

  public void setAllowCredentials(List<AssertionOptionsResponsePublicKeyAllowCredentialsInner> allowCredentials) {
    this.allowCredentials = allowCredentials;
  }

  public AssertionOptionsResponsePublicKey userVerification(UserVerificationEnum userVerification) {
    this.userVerification = userVerification;
    return this;
  }

  /**
   * Get userVerification
   * 
   * @return userVerification
   */

  @Schema(name = "userVerification", example = "preferred", required = false)
  public UserVerificationEnum getUserVerification() {
    return userVerification;
  }

  public void setUserVerification(UserVerificationEnum userVerification) {
    this.userVerification = userVerification;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AssertionOptionsResponsePublicKey assertionOptionsResponsePublicKey = (AssertionOptionsResponsePublicKey) o;
    return Objects.equals(this.challenge, assertionOptionsResponsePublicKey.challenge) &&
        Objects.equals(this.timeout, assertionOptionsResponsePublicKey.timeout) &&
        Objects.equals(this.rpId, assertionOptionsResponsePublicKey.rpId) &&
        Objects.equals(this.allowCredentials, assertionOptionsResponsePublicKey.allowCredentials) &&
        Objects.equals(this.userVerification, assertionOptionsResponsePublicKey.userVerification);
  }

  @Override
  public int hashCode() {
    return Objects.hash(challenge, timeout, rpId, allowCredentials, userVerification);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class AssertionOptionsResponsePublicKey {\n");
    sb.append("    challenge: ").append(toIndentedString(challenge)).append("\n");
    sb.append("    timeout: ").append(toIndentedString(timeout)).append("\n");
    sb.append("    rpId: ").append(toIndentedString(rpId)).append("\n");
    sb.append("    allowCredentials: ").append(toIndentedString(allowCredentials)).append("\n");
    sb.append("    userVerification: ").append(toIndentedString(userVerification)).append("\n");
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
