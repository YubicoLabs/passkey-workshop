package com.yubicolabs.bank_app.models.api;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import javax.validation.Valid;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import javax.annotation.Generated;

/**
 * AccountDetailsResponse
 */

@Builder
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-06-27T11:32:42.412827-05:00[America/Chicago]")
public class AccountDetailsResponse {

  @JsonProperty("accountId")
  private Integer accountId;

  @JsonProperty("balance")
  private BigDecimal balance;

  public AccountDetailsResponse accountId(Integer accountId) {
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

  public AccountDetailsResponse balance(BigDecimal balance) {
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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AccountDetailsResponse accountDetailsResponse = (AccountDetailsResponse) o;
    return Objects.equals(this.accountId, accountDetailsResponse.accountId) &&
        Objects.equals(this.balance, accountDetailsResponse.balance);
  }

  @Override
  public int hashCode() {
    return Objects.hash(accountId, balance);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class AccountDetailsResponse {\n");
    sb.append("    accountId: ").append(toIndentedString(accountId)).append("\n");
    sb.append("    balance: ").append(toIndentedString(balance)).append("\n");
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
