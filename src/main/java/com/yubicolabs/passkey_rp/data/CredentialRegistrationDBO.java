package com.yubicolabs.passkey_rp.data;

import java.time.Instant;

import lombok.Builder;
import lombok.Value;

/**
 * Object denoting a registration entry in the data source
 */
@Value
@Builder
public class CredentialRegistrationDBO {
  String username;
  String userHandle; // Should resolve to type ByteArray
  String credentialId; // Should resolve to type ByteArray
  String credential; // Should resolve to type CredentialRegistration
  Instant creationTime;
  Instant lastUsedDate;
  Instant lastUpdatedDate;
  Boolean isActive;
}
