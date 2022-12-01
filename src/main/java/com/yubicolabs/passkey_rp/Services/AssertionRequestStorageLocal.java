package com.yubicolabs.passkey_rp.Services;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;

import com.yubico.webauthn.data.ByteArray;
import com.yubicolabs.passkey_rp.data.AuthenticationRequest;

public class AssertionRequestStorageLocal implements AssertionRequestStorage {

  private Collection<AuthenticationRequest> authenticationRequestRepository = new HashSet<AuthenticationRequest>();

  @Override
  public Boolean insert(AuthenticationRequest request) {
    return authenticationRequestRepository.add(request);
  }

  @Override
  public Boolean invalidate(ByteArray requestID) {
    try {
      authenticationRequestRepository.stream()
          .filter(reg -> reg.getRequestId().equals(requestID))
          .forEach(reg -> reg.setIsActive(false));
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  @Override
  public Optional<AuthenticationRequest> getIfPresent(ByteArray requestID) {
    return authenticationRequestRepository.stream()
        .filter(reg -> reg.getRequestId().equals(requestID))
        .findFirst();
  }

}
