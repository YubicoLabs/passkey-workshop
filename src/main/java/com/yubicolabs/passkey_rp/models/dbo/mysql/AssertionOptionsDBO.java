package com.yubicolabs.passkey_rp.models.dbo.mysql;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Entity
@Table(name = "assertion_requests")
public class AssertionOptionsDBO {

  @Getter
  String assertionRequest;

  @Getter
  @Setter
  Boolean isActive;

  @Getter
  String requestId;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;
}
