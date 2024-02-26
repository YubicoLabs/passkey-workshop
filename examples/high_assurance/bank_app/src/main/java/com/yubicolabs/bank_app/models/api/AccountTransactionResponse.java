package com.yubicolabs.bank_app.models.api;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
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
 * AccountTransaction
 */

@Builder
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-06-30T13:35:17.173429-05:00[America/Chicago]")
public class AccountTransactionResponse {

  @JsonProperty("transactionId")
  private Integer transactionId;

  /**
   * Gets or Sets type
   */
  public enum TypeEnum {
    DEPOSIT("deposit"),

    WITHDRAW("withdraw"),

    TRANSFER("transfer");

    private String value;

    TypeEnum(String value) {
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
    public static TypeEnum fromValue(String value) {
      for (TypeEnum b : TypeEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
  }

  @JsonProperty("type")
  private TypeEnum type;

  @JsonProperty("amount")
  private BigDecimal amount;

  @JsonProperty("transactionDate")
  private String transactionDate;

  @JsonProperty("description")
  private String description;

  public AccountTransactionResponse transactionId(Integer transactionId) {
    this.transactionId = transactionId;
    return this;
  }

  /**
   * Get transactionId
   * 
   * @return transactionId
   */

  @Schema(name = "transactionId", example = "49583", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  public Integer getTransactionId() {
    return transactionId;
  }

  public void setTransactionId(Integer transactionId) {
    this.transactionId = transactionId;
  }

  public AccountTransactionResponse type(TypeEnum type) {
    this.type = type;
    return this;
  }

  /**
   * Get type
   * 
   * @return type
   */

  @Schema(name = "type", example = "transfer", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  public TypeEnum getType() {
    return type;
  }

  public void setType(TypeEnum type) {
    this.type = type;
  }

  public AccountTransactionResponse amount(BigDecimal amount) {
    this.amount = amount;
    return this;
  }

  /**
   * Get amount
   * 
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

  public AccountTransactionResponse transactionDate(String transactionDate) {
    this.transactionDate = transactionDate;
    return this;
  }

  /**
   * Get transactionDate
   * 
   * @return transactionDate
   */

  @Schema(name = "transactionDate", example = "06/19/2023 04:20pm", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  public String getTransactionDate() {
    return transactionDate;
  }

  public void setTransactionDate(String transactionDate) {
    this.transactionDate = transactionDate;
  }

  public AccountTransactionResponse description(String description) {
    this.description = description;
    return this;
  }

  /**
   * Get description
   * 
   * @return description
   */

  @Schema(name = "description", example = "birthday gift", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AccountTransactionResponse accountTransaction = (AccountTransactionResponse) o;
    return Objects.equals(this.transactionId, accountTransaction.transactionId) &&
        Objects.equals(this.type, accountTransaction.type) &&
        Objects.equals(this.amount, accountTransaction.amount) &&
        Objects.equals(this.transactionDate, accountTransaction.transactionDate) &&
        Objects.equals(this.description, accountTransaction.description);
  }

  @Override
  public int hashCode() {
    return Objects.hash(transactionId, type, amount, transactionDate, description);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class AccountTransaction {\n");
    sb.append("    transactionId: ").append(toIndentedString(transactionId)).append("\n");
    sb.append("    type: ").append(toIndentedString(type)).append("\n");
    sb.append("    amount: ").append(toIndentedString(amount)).append("\n");
    sb.append("    transactionDate: ").append(toIndentedString(transactionDate)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
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
