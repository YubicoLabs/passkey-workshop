package com.yubicolabs.passkey_rp.services.storage.mysql;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yubico.webauthn.AssertionRequest;
import com.yubicolabs.passkey_rp.interfaces.AssertionRequestStorage;
import com.yubicolabs.passkey_rp.models.common.AssertionOptions;
import com.yubicolabs.passkey_rp.models.dbo.mysql.AssertionOptionsDBO;

@Component
public class AssertionRequestStorage_mysql implements AssertionRequestStorage {

  @Autowired(required = false)
  private AssertionRequestRepositoryMysql assertionRequestRepositoryMysql;

  @Override
  public Boolean insert(AssertionRequest request, String requestId) {
    try {
      System.out.println(assertionRequestRepositoryMysql.getClass());
      AssertionOptionsDBO newItem = AssertionOptionsDBO.builder().assertionRequest(request.toJson())
          .requestId(requestId).isActive(true).build();
      assertionRequestRepositoryMysql.save(newItem);
      return true;
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }

  }

  @Override
  public Boolean invalidate(String requestID) {
    List<AssertionOptionsDBO> maybeList = assertionRequestRepositoryMysql.findByRequestId(requestID);

    if (maybeList.size() >= 1) {
      AssertionOptionsDBO request = maybeList.get(0);
      request.setIsActive(false);
      assertionRequestRepositoryMysql.save(request);
      return true;
    }
    return false;
  }

  @Override
  public Optional<AssertionOptions> getIfPresent(String requestID) {
    try {
      List<AssertionOptionsDBO> maybeList = assertionRequestRepositoryMysql.findByRequestId(requestID);

      if (maybeList.size() >= 1) {
        AssertionOptionsDBO request = maybeList.get(0);

        System.out.println("Found  request of ID: " + request.getRequestId());

        return Optional.ofNullable(
            AssertionOptions.builder().assertionRequest(AssertionRequest.fromJson(request.getAssertionRequest()))
                .requestId(request.getRequestId()).isActive(request.getIsActive()).build());
      }
      return Optional.empty();
    } catch (Exception e) {
      e.printStackTrace();
      return Optional.ofNullable(null);
    }

  }

}
