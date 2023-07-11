package com.yubicolabs.bank_app.models.dbo.mysql;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.Id;
import javax.persistence.PrePersist;
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
  @CreationTimestamp
  Date transactionCreateTime;

  @Getter
  boolean status;

  @Getter
  int accountId;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Getter
  Long id;

  @PrePersist
  protected void onCreate() {
    transactionCreateTime = new Date();
  }

}
