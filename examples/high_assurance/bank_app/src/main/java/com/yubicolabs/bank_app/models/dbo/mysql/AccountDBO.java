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
import lombok.Setter;

@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "account")

public class AccountDBO {

  @Getter
  String userHandle;

  @Getter
  @Setter
  Boolean advancedProtection;

  @Getter
  @Setter
  double balance;

  @Getter
  long createTime;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Getter
  Long id;
}
