package com.yubicolabs.passkey_rp.interfaces;

import java.util.Optional;

import com.yubico.webauthn.AssertionRequest;
import com.yubicolabs.passkey_rp.models.common.AssertionOptions;

public interface AssertionRequestStorage {
  /**
   * Add a new assertion request to storage
   * 
   * @param request created registration request to track for incoming
   *                registration
   * @return true if the request was successfully added, false otherwise
   */
  public Boolean insert(AssertionRequest request, String requestId);

  /**
   * Invalidate the designated request to prevent replay attacks from duplicate
   * authentication with the same request
   * 
   * @param requestID ID of the request to invalidate
   * @return true if the request was successfully invalidated, false otherwise
   */
  public Boolean invalidate(String requestID);

  /**
   * Get a request by ID
   * 
   * @param requestID ID of the request to retrieve
   * @return Optional object that contains an assertion request if present, empty
   *         otherwise
   */
  public Optional<AssertionOptions> getIfPresent(String requestID);
}
