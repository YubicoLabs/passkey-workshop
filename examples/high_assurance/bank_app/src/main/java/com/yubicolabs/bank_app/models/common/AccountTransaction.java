package com.yubicolabs.bank_app.models.common;

import java.time.Instant;

import lombok.Builder;
import lombok.Getter;
import lombok.Value;

@Builder
@Value
public class AccountTransaction {
  @Getter
  String type;

  @Getter
  double amount;

  @Getter
  String description;

  @Getter
  Instant createTime;

  @Getter
  Boolean status;

  @Getter
  int accountId;

  @Getter
  Long id;
}
