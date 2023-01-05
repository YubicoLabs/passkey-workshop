package com.yubicolabs.passkey_rp.interfaces;

import java.util.Optional;

import com.yubico.webauthn.data.ByteArray;
import com.yubicolabs.passkey_rp.data.AuthenticationRequest;

public interface AssertionRequestStorage {

  /**
   * Add a new assertion request to storage
   * 
   * @param request created registration request to track for incoming
   *                registration
   * @return true if the request was successfully added, false otherwise
   */
  public Boolean insert(AuthenticationRequest request);

  /**
   * Invalidate the designated request to prevent replay attacks from duplicate
   * authentication with the same request
   * 
   * @param requestID ID of the request to invalidate
   * @return true if the request was successfully invalidated, false otherwise
   */
  public Boolean invalidate(ByteArray requestID);

  /**
   * Get a request by ID
   * 
   * @param requestID ID of the request to retrieve
   * @return Optional object that contains an assertion request if present, empty
   *         otherwise
   */
  public Optional<AuthenticationRequest> getIfPresent(ByteArray requestID);
}
