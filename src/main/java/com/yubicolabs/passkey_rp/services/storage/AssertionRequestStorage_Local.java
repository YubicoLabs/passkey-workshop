package com.yubicolabs.passkey_rp.services.storage;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;

import com.yubico.webauthn.AssertionRequest;
import com.yubicolabs.passkey_rp.interfaces.AssertionRequestStorage;
import com.yubicolabs.passkey_rp.models.common.AssertionOptions;

public class AssertionRequestStorage_Local implements AssertionRequestStorage {

  private Collection<AssertionOptions> authenticationRequestRepository = new HashSet<AssertionOptions>();

  @Override
  public Boolean insert(AssertionRequest request, String requestId) {
    return authenticationRequestRepository
        .add(AssertionOptions.builder().assertionRequest(request).isActive(true).requestId(requestId).build());
  }

  @Override
  public Boolean invalidate(String requestID) {
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
  public Optional<AssertionOptions> getIfPresent(String requestID) {
    Optional<AssertionOptions> maybeRequest = authenticationRequestRepository.stream()
        .filter(reg -> reg.getRequestId().equals(requestID)).findFirst();

    if (maybeRequest.isPresent()) {
      return Optional.of(maybeRequest.get());
    } else {
      return Optional.empty();
    }
  }

}
