package com.yubicolabs.passkey_rp.models.dbo.mysql;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "advanced_protection_status")
public class AdvancedProtectionStatusDBO {

  @Getter
  @Column(columnDefinition = "text")
  String userHandle;

  @Getter
  @Setter
  Boolean isAdvancedProtection;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

}
