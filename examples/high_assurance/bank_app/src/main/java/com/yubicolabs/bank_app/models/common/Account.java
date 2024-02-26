package com.yubicolabs.bank_app.models.common;

import java.time.Instant;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder

public class Account {

  @Getter
  String userHandle;

  @Getter
  @Setter
  double balance;

  @Getter
  Instant createTime;

  @Getter
  Long id;
}
