package com.yubicolabs.passkey_rp.services.storage.mysql;

import java.util.List;
import java.util.Optional;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import com.yubicolabs.passkey_rp.interfaces.AdvancedProtectionStatusStorage;
import com.yubicolabs.passkey_rp.models.common.AdvancedProtectionStatus;
import com.yubicolabs.passkey_rp.models.dbo.mysql.AdvancedProtectionStatusDBO;

@Service
@ConditionalOnProperty(name = "datasource.type", havingValue = "mysql", matchIfMissing = true)
public class AdvancedProtectionStatusMysql implements AdvancedProtectionStatusStorage {

  private AdvancedProtectionStatusCrudRepository advancedProtectionStatusCrudRepository;

  public AdvancedProtectionStatusMysql(
      AdvancedProtectionStatusCrudRepository advancedProtectionStatusCrudRepository) {
    this.advancedProtectionStatusCrudRepository = advancedProtectionStatusCrudRepository;
  }

  @Override
  public boolean insert(AdvancedProtectionStatus newUser) {
    try {
      AdvancedProtectionStatusDBO newItem = AdvancedProtectionStatusDBO.builder().userHandle(newUser.getUserHandle())
          .isAdvancedProtection(newUser.isAdvancedProtection()).build();
      advancedProtectionStatusCrudRepository.save(newItem);
      return true;
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
  }

  @Override
  public boolean setAdvancedProtection(String userHandle, boolean setting) {
    try {
      Optional<AdvancedProtectionStatus> currentStatus = getIfPresent(userHandle);

      if (currentStatus.isPresent()) {
        AdvancedProtectionStatusDBO updateDBO = advancedProtectionStatusCrudRepository.findByUserHandle(userHandle)
            .get(0);

        updateDBO.setIsAdvancedProtection(setting);

        AdvancedProtectionStatusDBO newDBO = advancedProtectionStatusCrudRepository.save(updateDBO);

        if (newDBO.getIsAdvancedProtection() == setting) {
          return true;
        } else {
          return false;
        }
      } else {
        return false;
      }

    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
  }

  @Override
  public Optional<AdvancedProtectionStatus> getIfPresent(String userHandle) {
    try {
      List<AdvancedProtectionStatusDBO> maybeList = advancedProtectionStatusCrudRepository
          .findByUserHandle(userHandle);

      if (maybeList.size() >= 1) {
        AdvancedProtectionStatusDBO item = maybeList.get(0);

        return Optional.ofNullable(AdvancedProtectionStatus.builder().userHandle(item.getUserHandle())
            .isAdvancedProtection(item.getIsAdvancedProtection()).build());
      }
      return Optional.empty();
    } catch (Exception e) {
      e.printStackTrace();
      return Optional.ofNullable(null);
    }
  }

}
