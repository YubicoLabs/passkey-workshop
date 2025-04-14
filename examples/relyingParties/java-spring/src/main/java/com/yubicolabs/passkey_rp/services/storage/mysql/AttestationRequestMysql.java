package com.yubicolabs.passkey_rp.services.storage.mysql;

import java.util.List;
import java.util.Optional;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import com.yubico.webauthn.data.PublicKeyCredentialCreationOptions;
import com.yubicolabs.passkey_rp.interfaces.AttestationRequestStorage;
import com.yubicolabs.passkey_rp.models.common.AttestationOptions;
import com.yubicolabs.passkey_rp.models.dbo.mysql.AttestationOptionsDBO;

@Service
@ConditionalOnProperty(name = "datasource.type", havingValue = "mysql", matchIfMissing = true)
public class AttestationRequestMysql implements AttestationRequestStorage {

  private AttestationRequestCrudRepository attestationRequestCrudRepository;

  public AttestationRequestMysql(AttestationRequestCrudRepository attestationRequestCrudRepository) {
    this.attestationRequestCrudRepository = attestationRequestCrudRepository;
  }

  @Override
  public Boolean insert(PublicKeyCredentialCreationOptions request, String requestId) {
    try {
      AttestationOptionsDBO newItem = AttestationOptionsDBO.builder().attestationRequest(request.toJson())
          .requestId(requestId).isActive(true).build();

      attestationRequestCrudRepository.save(newItem);
      return true;
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
  }

  @Override
  public Boolean invalidate(String requestID) {
    List<AttestationOptionsDBO> maybeList = attestationRequestCrudRepository.findByRequestId(requestID);

    if (maybeList.size() >= 1) {
      AttestationOptionsDBO request = maybeList.get(0);
      request.setIsActive(false);
      attestationRequestCrudRepository.save(request);
      return true;
    }
    return false;
  }

  @Override
  public Optional<AttestationOptions> getIfPresent(String requestID) {
    try {
      List<AttestationOptionsDBO> maybeList = attestationRequestCrudRepository.findByRequestId(requestID);

      if (maybeList.size() >= 1) {
        AttestationOptionsDBO request = maybeList.get(0);

        return Optional.ofNullable(
            AttestationOptions.builder()
                .attestationRequest(PublicKeyCredentialCreationOptions.fromJson(request.getAttestationRequest()))
                .requestId(request.getRequestId()).isActive(request.getIsActive()).build());
      }
      return Optional.ofNullable(null);
    } catch (Exception e) {
      e.printStackTrace();
      return Optional.ofNullable(null);
    }
  }
}
