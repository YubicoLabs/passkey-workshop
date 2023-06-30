package com.yubicolabs.bank_app.models.api;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.math.BigDecimal;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.*;
import javax.annotation.Generated;

/**
 * Account
 */
@Builder
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-06-30T13:35:17.173429-05:00[America/Chicago]")
public class AccountResponse {

  @JsonProperty("accountId")
  private Integer accountId;

  @JsonProperty("balance")
  private BigDecimal balance;

  @JsonProperty("advancedProtection")
  private Boolean advancedProtection;

  public AccountResponse accountId(Integer accountId) {
    this.accountId = accountId;
    return this;
  }

  /**
   * Get accountId
   * 
   * @return accountId
   */

  @Schema(name = "accountId", example = "1234", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  public Integer getAccountId() {
    return accountId;
  }

  public void setAccountId(Integer accountId) {
    this.accountId = accountId;
  }

  public AccountResponse balance(BigDecimal balance) {
    this.balance = balance;
    return this;
  }

  /**
   * Get balance
   * 
   * @return balance
   */
  @Valid
  @Schema(name = "balance", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  public BigDecimal getBalance() {
    return balance;
  }

  public void setBalance(BigDecimal balance) {
    this.balance = balance;
  }

  public AccountResponse advancedProtection(Boolean advancedProtection) {
    this.advancedProtection = advancedProtection;
    return this;
  }

  /**
   * Get advancedProtection
   * 
   * @return advancedProtection
   */

  @Schema(name = "advancedProtection", example = "false", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  public Boolean getAdvancedProtection() {
    return advancedProtection;
  }

  public void setAdvancedProtection(Boolean advancedProtection) {
    this.advancedProtection = advancedProtection;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AccountResponse account = (AccountResponse) o;
    return Objects.equals(this.accountId, account.accountId) &&
        Objects.equals(this.balance, account.balance) &&
        Objects.equals(this.advancedProtection, account.advancedProtection);
  }

  @Override
  public int hashCode() {
    return Objects.hash(accountId, balance, advancedProtection);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Account {\n");
    sb.append("    accountId: ").append(toIndentedString(accountId)).append("\n");
    sb.append("    balance: ").append(toIndentedString(balance)).append("\n");
    sb.append("    advancedProtection: ").append(toIndentedString(advancedProtection)).append("\n");
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
