package com.yubicolabs.bank_app.models.common;

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
  long createTime;

  @Getter
  Boolean status;

  @Getter
  int accountId;

  @Getter
  Long id;
}
