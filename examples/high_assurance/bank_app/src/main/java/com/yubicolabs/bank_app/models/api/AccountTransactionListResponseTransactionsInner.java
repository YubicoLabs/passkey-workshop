package com.yubicolabs.bank_app.models.api;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.math.BigDecimal;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * AccountTransactionListResponseTransactionsInner
 */

@JsonTypeName("AccountTransactionListResponse_transactions_inner")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-06-27T11:32:42.412827-05:00[America/Chicago]")
public class AccountTransactionListResponseTransactionsInner {

  @JsonProperty("transactionId")
  private Integer transactionId;

  @JsonProperty("type")
  private String type;

  @JsonProperty("amount")
  private BigDecimal amount;

  @JsonProperty("transactionDate")
  private String transactionDate;

  public AccountTransactionListResponseTransactionsInner transactionId(Integer transactionId) {
    this.transactionId = transactionId;
    return this;
  }

  /**
   * Get transactionId
   * @return transactionId
  */
  
  @Schema(name = "transactionId", example = "49583", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  public Integer getTransactionId() {
    return transactionId;
  }

  public void setTransactionId(Integer transactionId) {
    this.transactionId = transactionId;
  }

  public AccountTransactionListResponseTransactionsInner type(String type) {
    this.type = type;
    return this;
  }

  /**
   * Get type
   * @return type
  */
  
  @Schema(name = "type", example = "transfer", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public AccountTransactionListResponseTransactionsInner amount(BigDecimal amount) {
    this.amount = amount;
    return this;
  }

  /**
   * Get amount
   * @return amount
  */
  @Valid 
  @Schema(name = "amount", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  public BigDecimal getAmount() {
    return amount;
  }

  public void setAmount(BigDecimal amount) {
    this.amount = amount;
  }

  public AccountTransactionListResponseTransactionsInner transactionDate(String transactionDate) {
    this.transactionDate = transactionDate;
    return this;
  }

  /**
   * Get transactionDate
   * @return transactionDate
  */
  
  @Schema(name = "transactionDate", example = "06/19/2023 04:20pm", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  public String getTransactionDate() {
    return transactionDate;
  }

  public void setTransactionDate(String transactionDate) {
    this.transactionDate = transactionDate;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AccountTransactionListResponseTransactionsInner accountTransactionListResponseTransactionsInner = (AccountTransactionListResponseTransactionsInner) o;
    return Objects.equals(this.transactionId, accountTransactionListResponseTransactionsInner.transactionId) &&
        Objects.equals(this.type, accountTransactionListResponseTransactionsInner.type) &&
        Objects.equals(this.amount, accountTransactionListResponseTransactionsInner.amount) &&
        Objects.equals(this.transactionDate, accountTransactionListResponseTransactionsInner.transactionDate);
  }

  @Override
  public int hashCode() {
    return Objects.hash(transactionId, type, amount, transactionDate);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class AccountTransactionListResponseTransactionsInner {\n");
    sb.append("    transactionId: ").append(toIndentedString(transactionId)).append("\n");
    sb.append("    type: ").append(toIndentedString(type)).append("\n");
    sb.append("    amount: ").append(toIndentedString(amount)).append("\n");
    sb.append("    transactionDate: ").append(toIndentedString(transactionDate)).append("\n");
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

