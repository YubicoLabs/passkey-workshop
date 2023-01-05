package com.yubicolabs.passkey_rp.interfaces;

import java.util.Collection;
import java.util.Optional;

import com.yubico.webauthn.data.ByteArray;
import com.yubicolabs.passkey_rp.data.RegistrationRequest;

public interface RegistrationRequestStorage {

  /**
   * Add a new registration request to storage
   * 
   * @param request created registration request to track for incoming
   *                registration
   * @return true if the registration was added to storage, and false otherwise
   */
  public Boolean insert(RegistrationRequest request);

  /**
   * Invalidate the designated request to prevent replay attacks from duplicate
   * registrations with the same request
   * 
   * @param requestID ID of the request to invalidate
   * @return true if the request was successfully invalidated, false otherwise
   */
  public Boolean invalidate(ByteArray requestID);

  /**
   * Get a registration request if it exists in storage
   * 
   * @param requestID ID of the request to retrieve
   * @return Optional object that may include a registration request if found,
   *         empty otherwise
   */
  public Optional<RegistrationRequest> getIfPresent(ByteArray requestID);

  public Collection<RegistrationRequest> getAll();

}
