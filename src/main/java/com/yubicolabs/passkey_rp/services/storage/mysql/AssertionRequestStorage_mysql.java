package com.yubicolabs.passkey_rp.services.storage.mysql;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yubico.webauthn.AssertionRequest;
import com.yubicolabs.passkey_rp.interfaces.AssertionRequestStorage;
import com.yubicolabs.passkey_rp.models.common.AssertionOptions;
import com.yubicolabs.passkey_rp.models.dbo.mysql.AssertionOptionsDBO;

@Component
public class AssertionRequestStorage_mysql implements AssertionRequestStorage {

  @Autowired(required = true)
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
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Optional<AssertionOptions> getIfPresent(String requestID) {
    // TODO Auto-generated method stub
    return Optional.empty();
  }

}
