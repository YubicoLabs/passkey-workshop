package com.yubicolabs.passkey_rp.models.api;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import javax.annotation.Generated;

/**
 * AssertionResultResponse
 */
@Builder
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-01-31T11:58:50.125043-06:00[America/Chicago]")
public class AssertionResultResponse {

  @JsonProperty("status")
  private String status;

  public AssertionResultResponse status(String status) {
    this.status = status;
    return this;
  }

  /**
   * loa = Level of Assurance
   */
  public enum loaEnum {
    HIGH(2),
    LOW(1);

    private int value;

    loaEnum(int value) {
      this.value = value;
    }

    @JsonValue
    public int getValue() {
      return value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }

    @JsonCreator
    public static loaEnum fromValue(int value) {
      for (loaEnum b : loaEnum.values()) {
        if (b.value == value) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }

  }

  @JsonProperty("loa")
  private loaEnum loa = loaEnum.LOW;

  public AssertionResultResponse loa(loaEnum loa) {
    this.loa = loa;
    return this;
  }

  /**
   * Get status
   * 
   * @return status
   */

  @Schema(name = "status", example = "ok", required = false)
  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  /**
   * Get loa
   * 
   * @return loa
   */

  @Schema(name = "loa", example = "1", required = false)
  public loaEnum getloa() {
    return loa;
  }

  public void setloa(loaEnum loa) {
    this.loa = loa;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AssertionResultResponse assertionResultResponse = (AssertionResultResponse) o;
    return Objects.equals(this.status, assertionResultResponse.status)
        && Objects.equals(this.loa, assertionResultResponse.loa);
  }

  @Override
  public int hashCode() {
    return Objects.hash(status);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class AssertionResultResponse {\n");
    sb.append("    status: ").append(toIndentedString(status)).append("\n");
    sb.append("    loa: ").append(toIndentedString(toIndentedString(loa))).append("\n");
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
