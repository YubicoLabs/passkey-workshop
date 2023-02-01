package com.yubicolabs.passkey_rp.services.storage;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;

import com.yubico.webauthn.data.PublicKeyCredentialCreationOptions;
import com.yubicolabs.passkey_rp.interfaces.AttestationRequestStorage;
import com.yubicolabs.passkey_rp.models.dbo.AttestationOptionsDBO;

public class AttestationRequestStorage_Local implements AttestationRequestStorage {

  private Collection<AttestationOptionsDBO> attestationRequestRepository = new HashSet<AttestationOptionsDBO>();

  @Override
  public Boolean insert(PublicKeyCredentialCreationOptions request, String requestId) {
    return attestationRequestRepository
        .add(AttestationOptionsDBO.builder().pkc(request).isActive(true).requestId(requestId).build());
  }

  @Override
  public Boolean invalidate(String requestID) {
    try {
      attestationRequestRepository.stream()
          .filter(reg -> reg.getRequestId().equals(requestID))
          .forEach(reg -> reg.setIsActive(false));
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  @Override
  public Optional<AttestationOptionsDBO> getIfPresent(String requestID) {
    Optional<AttestationOptionsDBO> maybeRequest = attestationRequestRepository.stream()
        .filter(reg -> reg.getRequestId().equals(requestID)).findFirst();

    if (maybeRequest.isPresent()) {
      return Optional.of(maybeRequest.get());
    } else {
      return Optional.empty();
    }
  }

}
