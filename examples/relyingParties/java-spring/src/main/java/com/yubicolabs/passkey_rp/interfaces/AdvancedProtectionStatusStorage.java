package com.yubicolabs.passkey_rp.interfaces;

import java.util.Optional;

import com.yubicolabs.passkey_rp.models.common.AdvancedProtectionStatus;

public interface AdvancedProtectionStatusStorage {
  /**
   * Add a new assertion request to storage
   * 
   * @param newUser new user created to keep track of status
   * @return true if the request was successfully added, false otherwise
   */
  public boolean insert(AdvancedProtectionStatus newUser);

  /**
   * Update the setting of an accounts advanced protection setting
   * 
   * @param userHandle Userhandle being updated
   * @param setting    The desired AP setting for the account
   *                   Note - It is possible to set an account with true to true,
   *                   this will still result in a successful request (same with
   *                   false to false)
   * @return true if the account was updated, false otherwise
   */
  public boolean setAdvancedProtection(String userHandle, boolean setting);

  /**
   * Get status by user handle
   * 
   * @param userHandle Userhandle being evaluated
   * @return Optional object that contains an the advanced protection status for a
   *         specific user handle
   */
  public Optional<AdvancedProtectionStatus> getIfPresent(String userHandle);
}
