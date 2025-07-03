package com.yubicolabs.passkey_rp.models.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * AssertionResultResponse
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class AssertionResultResponse {
  private String status;
  private loaEnum loa = loaEnum.LOW;

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
}
