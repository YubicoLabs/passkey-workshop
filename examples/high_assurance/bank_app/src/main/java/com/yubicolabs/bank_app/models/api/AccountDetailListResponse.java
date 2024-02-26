package com.yubicolabs.bank_app.models.api;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.yubicolabs.bank_app.models.api.AccountResponse;
import java.util.ArrayList;
import java.util.List;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.*;
import javax.annotation.Generated;

/**
 * AccountDetailListResponse
 */
@Builder
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-06-30T13:35:17.173429-05:00[America/Chicago]")
public class AccountDetailListResponse {

  @JsonProperty("accounts")
  @Valid
  private List<AccountResponse> accounts = null;

  public AccountDetailListResponse accounts(List<AccountResponse> accounts) {
    this.accounts = accounts;
    return this;
  }

  public AccountDetailListResponse addAccountsItem(AccountResponse accountsItem) {
    if (this.accounts == null) {
      this.accounts = new ArrayList<>();
    }
    this.accounts.add(accountsItem);
    return this;
  }

  /**
   * Get accounts
   * 
   * @return accounts
   */
  @Valid
  @Schema(name = "accounts", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  public List<AccountResponse> getAccounts() {
    return accounts;
  }

  public void setAccounts(List<AccountResponse> accounts) {
    this.accounts = accounts;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AccountDetailListResponse accountDetailListResponse = (AccountDetailListResponse) o;
    return Objects.equals(this.accounts, accountDetailListResponse.accounts);
  }

  @Override
  public int hashCode() {
    return Objects.hash(accounts);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class AccountDetailListResponse {\n");
    sb.append("    accounts: ").append(toIndentedString(accounts)).append("\n");
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
