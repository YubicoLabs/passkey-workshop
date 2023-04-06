package com.yubicolabs.passkey_rp.models.api;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.OffsetDateTime;
import org.springframework.format.annotation.DateTimeFormat;
import javax.validation.Valid;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.annotation.Generated;

/**
 * UserProfileResponse
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-01-31T11:58:50.125043-06:00[America/Chicago]")
public class UserProfileResponse {

  @JsonProperty("id")
  private Integer id;

  @JsonProperty("userName")
  private String userName;

  @JsonProperty("displayName")
  private String displayName;

  @JsonProperty("lastLoginDate")
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime lastLoginDate;

  public UserProfileResponse id(Integer id) {
    this.id = id;
    return this;
  }

  /**
   * Get id
   * 
   * @return id
   */

  @Schema(name = "id", example = "34545132", required = false)
  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public UserProfileResponse userName(String userName) {
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

  public UserProfileResponse displayName(String displayName) {
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

  public UserProfileResponse lastLoginDate(OffsetDateTime lastLoginDate) {
    this.lastLoginDate = lastLoginDate;
    return this;
  }

  /**
   * Get lastLoginDate
   * 
   * @return lastLoginDate
   */
  @Valid
  @Schema(name = "lastLoginDate", example = "2023-01-09T13:44:02Z", required = false)
  public OffsetDateTime getLastLoginDate() {
    return lastLoginDate;
  }

  public void setLastLoginDate(OffsetDateTime lastLoginDate) {
    this.lastLoginDate = lastLoginDate;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    UserProfileResponse userProfileResponse = (UserProfileResponse) o;
    return Objects.equals(this.id, userProfileResponse.id) &&
        Objects.equals(this.userName, userProfileResponse.userName) &&
        Objects.equals(this.displayName, userProfileResponse.displayName) &&
        Objects.equals(this.lastLoginDate, userProfileResponse.lastLoginDate);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, userName, displayName, lastLoginDate);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class UserProfileResponse {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    userName: ").append(toIndentedString(userName)).append("\n");
    sb.append("    displayName: ").append(toIndentedString(displayName)).append("\n");
    sb.append("    lastLoginDate: ").append(toIndentedString(lastLoginDate)).append("\n");
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
