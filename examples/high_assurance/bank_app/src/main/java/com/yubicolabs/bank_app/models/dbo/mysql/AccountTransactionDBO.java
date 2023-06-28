package com.yubicolabs.bank_app.models.dbo.mysql;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "account_transaction")

public class AccountTransactionDBO {

  @Getter
  String type;

  @Getter
  double amount;

  @Getter
  String description;

  @Getter
  long createTime;

  @Getter
  boolean status;

  @Getter
  int accountId;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Getter
  Long id;

}
