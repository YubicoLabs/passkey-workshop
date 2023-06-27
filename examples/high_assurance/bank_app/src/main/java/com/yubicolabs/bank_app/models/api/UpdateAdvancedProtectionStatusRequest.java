package com.yubicolabs.bank_app.models.api;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * UpdateAdvancedProtectionStatusRequest
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-06-27T11:32:42.412827-05:00[America/Chicago]")
public class UpdateAdvancedProtectionStatusRequest {

  @JsonProperty("accountId")
  private Integer accountId;

  @JsonProperty("enabled")
  private Boolean enabled;

  public UpdateAdvancedProtectionStatusRequest accountId(Integer accountId) {
    this.accountId = accountId;
    return this;
  }

  /**
   * Get accountId
   * @return accountId
  */
  
  @Schema(name = "accountId", example = "1349", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  public Integer getAccountId() {
    return accountId;
  }

  public void setAccountId(Integer accountId) {
    this.accountId = accountId;
  }

  public UpdateAdvancedProtectionStatusRequest enabled(Boolean enabled) {
    this.enabled = enabled;
    return this;
  }

  /**
   * Get enabled
   * @return enabled
  */
  
  @Schema(name = "enabled", example = "true", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
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
    UpdateAdvancedProtectionStatusRequest updateAdvancedProtectionStatusRequest = (UpdateAdvancedProtectionStatusRequest) o;
    return Objects.equals(this.accountId, updateAdvancedProtectionStatusRequest.accountId) &&
        Objects.equals(this.enabled, updateAdvancedProtectionStatusRequest.enabled);
  }

  @Override
  public int hashCode() {
    return Objects.hash(accountId, enabled);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class UpdateAdvancedProtectionStatusRequest {\n");
    sb.append("    accountId: ").append(toIndentedString(accountId)).append("\n");
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

