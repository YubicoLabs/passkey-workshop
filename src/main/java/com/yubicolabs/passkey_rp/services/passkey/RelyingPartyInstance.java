package com.yubicolabs.passkey_rp.services.passkey;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.yubico.webauthn.RelyingParty;
import com.yubico.webauthn.data.AttestationConveyancePreference;
import com.yubico.webauthn.data.RelyingPartyIdentity;
import com.yubicolabs.passkey_rp.services.storage.StorageInstance;

import lombok.Getter;

@Service
@Scope("singleton")
public class RelyingPartyInstance {

  @Getter
  @Autowired
  private StorageInstance storageInstance;

  @Getter
  private RelyingParty relyingParty;

  // @TODO - Read these value from ENV

  @PostConstruct
  private void setRPInstance() {
    this.relyingParty = RelyingParty.builder()
        .identity(generateIdentity())
        .credentialRepository(storageInstance.getCredentialStorage())
        .origins(generateOrigins())
        .attestationConveyancePreference(AttestationConveyancePreference.DIRECT)
        .allowUntrustedAttestation(true)
        .validateSignatureCounter(true)
        .build();
  }

  private RelyingPartyIdentity generateIdentity() {
    return RelyingPartyIdentity.builder()
        .id("localhost")
        .name("My passkey app")
        .build();
  }

  private Set<String> generateOrigins() {
    Set<String> allowedOrigins = new HashSet<String>();
    allowedOrigins.add("http://localhost:3000");
    return allowedOrigins;
  }

}
