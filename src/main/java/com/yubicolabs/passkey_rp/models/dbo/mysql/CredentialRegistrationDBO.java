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
@Table(name = "credential_registrations")
public class CredentialRegistrationDBO {

  @Getter
  @Column(columnDefinition = "text")
  String userHandle;

  @Getter
  @Column(columnDefinition = "text")
  String credentialID;

  @Getter
  @Column(columnDefinition = "text")
  String userIdentity;

  @Getter
  @Setter
  @Column(columnDefinition = "text")
  String credentialNickname;

  @Getter
  long registrationTime;

  @Getter
  long lastUsedTime;

  @Getter
  long lastUpdateTime;

  @Getter
  @Column(columnDefinition = "text")
  String credential;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;
}
