package com.yubicolabs.passkey_rp.models.api;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import javax.annotation.Generated;

/**
 * UpdateAdvancedProtectionStatusResponse
 */

@Builder
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-06-27T11:32:42.412827-05:00[America/Chicago]")
public class UpdateAdvancedProtectionStatusResponse {

  @JsonProperty("enabled")
  private boolean enabled;

  public UpdateAdvancedProtectionStatusResponse status(boolean status) {
    this.enabled = status;
    return this;
  }

  /**
   * Get status
   * 
   * @return status
   */

  @Schema(name = "enabled", example = "complete", required = false)
  public boolean getEnabled() {
    return enabled;
  }

  public void setStatus(boolean enabled) {
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
    UpdateAdvancedProtectionStatusResponse updateAdvancedProtectionStatusResponse = (UpdateAdvancedProtectionStatusResponse) o;
    return Objects.equals(this.enabled, updateAdvancedProtectionStatusResponse.enabled);
  }

  @Override
  public int hashCode() {
    return Objects.hash(enabled);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class UpdateAdvancedProtectionStatusResponse {\n");
    sb.append("    Enabled: ").append(toIndentedString(enabled)).append("\n");
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