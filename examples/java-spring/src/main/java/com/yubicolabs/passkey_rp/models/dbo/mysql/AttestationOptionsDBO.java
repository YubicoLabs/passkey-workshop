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
@Table(name = "attestation_requests")
public class AttestationOptionsDBO {
  @Getter
  @Column(columnDefinition = "text")
  String attestationRequest;

  @Getter
  @Setter
  Boolean isActive;

  @Getter
  @Column(columnDefinition = "text")
  String requestId;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;
}
