package com.yubicolabs.passkey_rp.Services;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;

import com.yubico.webauthn.data.ByteArray;
import com.yubicolabs.passkey_rp.data.RegistrationRequest;
import com.yubicolabs.passkey_rp.interfaces.RegistrationRequestStorage;

public class RegistrationRequestStorageLocal implements RegistrationRequestStorage {

  private Collection<RegistrationRequest> registrationRequestRepository = new HashSet<RegistrationRequest>();

  @Override
  public Boolean insert(RegistrationRequest request) {
    return registrationRequestRepository.add(request);
  }

  @Override
  public Boolean invalidate(ByteArray requestID) {
    try {
      registrationRequestRepository.stream()
          .filter(reg -> reg.getRequestId().equals(requestID))
          .forEach(reg -> reg.setIsActive(false));
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  @Override
  public Optional<RegistrationRequest> getIfPresent(ByteArray requestID) {
    return registrationRequestRepository.stream()
        .filter(reg -> reg.getRequestId().equals(requestID))
        .findFirst();
  }

  @Override
  public Collection<RegistrationRequest> getAll() {
    return registrationRequestRepository;
  }

}
