package com.yubicolabs.passkey_rp.models.api;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import javax.annotation.Generated;

/**
 * AdvancedProtection
 */

@Builder
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-06-30T13:35:17.173429-05:00[America/Chicago]")
public class AdvancedProtection {

  @JsonProperty("userHandle")
  private String userHandle;

  @JsonProperty("enabled")
  private Boolean enabled;

  public AdvancedProtection userHandle(String userHandle) {
    this.userHandle = userHandle;
    return this;
  }

  /**
   * Get userHandle
   * 
   * @return userHandle
   */

  @Schema(name = "userHandle", example = "someNameHere", required = false)
  public String getUserHandle() {
    return userHandle;
  }

  public void setUserHandle(String userHandle) {
    this.userHandle = userHandle;
  }

  public AdvancedProtection enabled(Boolean enabled) {
    this.enabled = enabled;
    return this;
  }

  /**
   * Get enabled
   * 
   * @return enabled
   */

  @Schema(name = "enabled", example = "true", required = false)
  public Boolean getEnabled() {
    return enabled;
  }

  public void setEnabled(Boolean enabled) {
    this.enabled = enabled;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AdvancedProtection advancedProtection = (AdvancedProtection) o;
    return Objects.equals(this.userHandle, advancedProtection.userHandle) &&
        Objects.equals(this.enabled, advancedProtection.enabled);
  }

  @Override
  public int hashCode() {
    return Objects.hash(userHandle, enabled);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class AdvancedProtection {\n");
    sb.append("    userHandle: ").append(toIndentedString(userHandle)).append("\n");
    sb.append("    enabled: ").append(toIndentedString(enabled)).append("\n");
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
