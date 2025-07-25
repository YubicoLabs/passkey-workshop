package com.yubicolabs.passkey_rp.models.api;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.time.OffsetDateTime;
import org.springframework.format.annotation.DateTimeFormat;
import javax.validation.Valid;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import javax.annotation.Generated;

/**
 * UserCredentialsResponseCredentialsInner
 */

@JsonTypeName("UserCredentialsResponse_credentials_inner")
@Builder
public class UserCredentialsResponseCredentialsInner {

  @JsonProperty("id")
  private String id;

  @JsonProperty("type")
  private String type;

  @JsonProperty("nickName")
  private String nickName;

  @JsonProperty("registrationTime")
  private long registrationTime;

  @JsonProperty("lastUsedTime")
  private long lastUsedTime;

  public UserCredentialsResponseCredentialsInner id(String id) {
    this.id = id;
    return this;
  }

  @JsonProperty("iconURI")
  private String iconURI;

  @JsonProperty("isHighAssurance")
  private boolean isHighAssurance;

  @JsonProperty("state")
  private String state;

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

  public UserCredentialsResponseCredentialsInner type(String type) {
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

  public UserCredentialsResponseCredentialsInner nickName(String nickName) {
    this.nickName = nickName;
    return this;
  }

  /**
   * Get nickName
   * 
   * @return nickName
   */

  @Schema(name = "nickName", example = "YubiKey 5Ci", required = false)
  public String getNickName() {
    return nickName;
  }

  public void setNickName(String nickName) {
    this.nickName = nickName;
  }

  public UserCredentialsResponseCredentialsInner registrationTime(long registrationTime) {
    this.registrationTime = registrationTime;
    return this;
  }

  /**
   * Get registrationTime
   * 
   * @return registrationTime
   */
  @Valid
  @Schema(name = "registrationTime", example = "2022-07-21T17:32:28Z", required = false)
  public long getRegistrationTime() {
    return registrationTime;
  }

  public void setRegistrationTime(long registrationTime) {
    this.registrationTime = registrationTime;
  }

  public UserCredentialsResponseCredentialsInner lastUsedTime(long lastUsedTime) {
    this.lastUsedTime = lastUsedTime;
    return this;
  }

  /**
   * Get lastUsedTime
   * 
   * @return lastUsedTime
   */
  @Valid
  @Schema(name = "lastUsedTime", example = "2022-07-21T18:15:06Z", required = false)
  public long getLastUsedTime() {
    return lastUsedTime;
  }

  public void setLastUsedTime(long lastUsedTime) {
    this.lastUsedTime = lastUsedTime;
  }

  /**
   * Get iconURI
   * 
   * @return iconURI
   */

  @Schema(name = "iconURI", example = "YubiKey 5Ci", required = false)
  public String getIconURI() {
    return iconURI;
  }

  public void setIconURI(String iconURI) {
    this.iconURI = iconURI;
  }

  /**
   * Get isHighAssurance
   * 
   * @return isHighAssurance
   */

  @Schema(name = "isHighAssurance", example = "false", required = false)
  public boolean getIsHighAssurance() {
    return isHighAssurance;
  }

  public void setIsHighAssurance(boolean isHighAssurance) {
    this.isHighAssurance = isHighAssurance;
  }

  /**
   * Get state
   * 
   * @return state
   */

  @Schema(name = "state", example = "ENABLED", required = false)
  public String getState() {
    return state;
  }

  public void setState(String state) {
    this.state = state;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    UserCredentialsResponseCredentialsInner userCredentialsResponseCredentialsInner = (UserCredentialsResponseCredentialsInner) o;
    return Objects.equals(this.id, userCredentialsResponseCredentialsInner.id) &&
        Objects.equals(this.type, userCredentialsResponseCredentialsInner.type) &&
        Objects.equals(this.nickName, userCredentialsResponseCredentialsInner.nickName) &&
        Objects.equals(this.registrationTime, userCredentialsResponseCredentialsInner.registrationTime) &&
        Objects.equals(this.lastUsedTime, userCredentialsResponseCredentialsInner.lastUsedTime) &&
        Objects.equals(this.iconURI, userCredentialsResponseCredentialsInner.iconURI) &&
        Objects.equals(this.isHighAssurance, userCredentialsResponseCredentialsInner.isHighAssurance) &&
        Objects.equals(this.state, userCredentialsResponseCredentialsInner.state);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, type, nickName, registrationTime, lastUsedTime);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class UserCredentialsResponseCredentialsInner {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    type: ").append(toIndentedString(type)).append("\n");
    sb.append("    nickName: ").append(toIndentedString(nickName)).append("\n");
    sb.append("    registrationTime: ").append(toIndentedString(registrationTime)).append("\n");
    sb.append("    lastUsedTime: ").append(toIndentedString(lastUsedTime)).append("\n");
    sb.append("    iconURI: ").append(toIndentedString(iconURI)).append("\n");
    sb.append("    isHighAssurance: ").append(toIndentedString(isHighAssurance)).append("\n");
    sb.append("    state: ").append(toIndentedString(state)).append("\n");
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
