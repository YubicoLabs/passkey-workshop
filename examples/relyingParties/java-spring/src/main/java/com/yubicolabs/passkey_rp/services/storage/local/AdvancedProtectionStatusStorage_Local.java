package com.yubicolabs.passkey_rp.services.storage.local;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;

import com.yubicolabs.passkey_rp.interfaces.AdvancedProtectionStatusStorage;
import com.yubicolabs.passkey_rp.models.common.AdvancedProtectionStatus;

public class AdvancedProtectionStatusStorage_Local implements AdvancedProtectionStatusStorage {

  private Collection<AdvancedProtectionStatus> advancedProtectionStatusRepository = new HashSet<AdvancedProtectionStatus>();

  @Override
  public boolean insert(AdvancedProtectionStatus newUser) {
    return advancedProtectionStatusRepository.add(newUser);
  }

  @Override
  public boolean setAdvancedProtection(String userHandle, boolean setting) {
    advancedProtectionStatusRepository.stream()
        .filter(currentUser -> currentUser.equals(userHandle))
        .forEach(currentUser -> currentUser.setAdvancedProtection(setting));
    return setting == getIfPresent(userHandle).get().isAdvancedProtection();

  }

  @Override
  public Optional<AdvancedProtectionStatus> getIfPresent(String userHandle) {
    return advancedProtectionStatusRepository.stream()
        .filter(currentUser -> currentUser.equals(userHandle)).findFirst();
  }
}