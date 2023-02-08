package com.yubicolabs.passkey_rp.models.dbo.mysql;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class CredentialRegistrationDBO {
  String userHandle;
  String credential;
}
