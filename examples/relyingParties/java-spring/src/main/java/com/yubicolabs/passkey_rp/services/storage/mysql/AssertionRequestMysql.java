package com.yubicolabs.passkey_rp.services.storage.mysql;

import java.util.List;
import java.util.Optional;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import com.yubico.webauthn.AssertionRequest;
import com.yubicolabs.passkey_rp.interfaces.AssertionRequestStorage;
import com.yubicolabs.passkey_rp.models.common.AssertionOptions;
import com.yubicolabs.passkey_rp.models.dbo.mysql.AssertionOptionsDBO;

@Service
@ConditionalOnProperty(name = "datasource.type", havingValue = "mysql", matchIfMissing = true)
public class AssertionRequestMysql implements AssertionRequestStorage {

  private AssertionRequestCrudRepository assertionRequestCrudRepository;

  public AssertionRequestMysql(AssertionRequestCrudRepository assertionRequestCrudRepository) {
    this.assertionRequestCrudRepository = assertionRequestCrudRepository;
  }

  @Override
  public Boolean insert(AssertionRequest request, String requestId) {
    try {
      AssertionOptionsDBO newItem = AssertionOptionsDBO.builder().assertionRequest(request.toJson())
          .requestId(requestId).isActive(true).build();
      assertionRequestCrudRepository.save(newItem);
      return true;
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }

  }

  @Override
  public Boolean invalidate(String requestID) {
    List<AssertionOptionsDBO> maybeList = assertionRequestCrudRepository.findByRequestId(requestID);

    if (maybeList.size() >= 1) {
      AssertionOptionsDBO request = maybeList.get(0);
      request.setIsActive(false);
      assertionRequestCrudRepository.save(request);
      return true;
    }
    return false;
  }

  @Override
  public Optional<AssertionOptions> getIfPresent(String requestID) {
    try {
      List<AssertionOptionsDBO> maybeList = assertionRequestCrudRepository.findByRequestId(requestID);

      if (maybeList.size() >= 1) {
        AssertionOptionsDBO request = maybeList.get(0);

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
