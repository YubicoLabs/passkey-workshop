package com.yubicolabs.passkey_rp.services.storage.mysql;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yubicolabs.passkey_rp.interfaces.AdvancedProtectionStatusStorage;
import com.yubicolabs.passkey_rp.models.common.AdvancedProtectionStatus;
import com.yubicolabs.passkey_rp.models.dbo.mysql.AdvancedProtectionStatusDBO;

@Component
public class AdvancedProtectionStatusReepository_mysql implements AdvancedProtectionStatusStorage {

  @Autowired
  private AdvancedProtectionStatusRepositoryMysql advancedProtectionStatusRepositoryMysql;

  @Override
  public boolean insert(AdvancedProtectionStatus newUser) {
    try {
      AdvancedProtectionStatusDBO newItem = AdvancedProtectionStatusDBO.builder().userHandle(newUser.getUserHandle())
          .isAdvancedProtection(newUser.isAdvancedProtection()).build();
      advancedProtectionStatusRepositoryMysql.save(newItem);
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
        AdvancedProtectionStatusDBO updateDBO = advancedProtectionStatusRepositoryMysql.findByUserHandle(userHandle)
            .get(0);

        updateDBO.setIsAdvancedProtection(setting);

        AdvancedProtectionStatusDBO newDBO = advancedProtectionStatusRepositoryMysql.save(updateDBO);

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
      List<AdvancedProtectionStatusDBO> maybeList = advancedProtectionStatusRepositoryMysql
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
