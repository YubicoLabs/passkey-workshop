package com.yubicolabs.passkey_rp.services.storage.local;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.yubico.webauthn.RegisteredCredential;
import com.yubico.webauthn.data.ByteArray;
import com.yubico.webauthn.data.PublicKeyCredentialDescriptor;
import com.yubicolabs.passkey_rp.interfaces.CredentialStorage;
import com.yubicolabs.passkey_rp.models.common.CredentialRegistration;
import com.yubicolabs.passkey_rp.models.common.CredentialRegistration.StateEnum;

public class CredentialStorage_Local implements CredentialStorage {

  private Collection<CredentialRegistration> credentialRepository = new HashSet<CredentialRegistration>();

  @Override
  public Set<PublicKeyCredentialDescriptor> getCredentialIdsForUsername(String username) {
    return getRegistrationsByUsername(username)
        .stream()
        .filter(cred -> cred.getState().getValue().equals(StateEnum.ENABLED.getValue()))
        .map(reg -> PublicKeyCredentialDescriptor.builder()
            .id(reg.getCredential().getCredentialId())
            .build())
        .collect(Collectors.toSet());
  }

  @Override
  public Optional<ByteArray> getUserHandleForUsername(String username) {
    return getRegistrationsByUsername(username).stream().findAny().map(reg -> reg.getUserIdentity().getId());
  }

  @Override
  public Optional<String> getUsernameForUserHandle(ByteArray userHandle) {
    return getRegistrationsByUserHandle(userHandle).stream().findAny().map(reg -> reg.getUserIdentity().getName());
  }

  @Override
  public Optional<RegisteredCredential> lookup(ByteArray credentialId, ByteArray userHandle) {
    Optional<CredentialRegistration> registrationMaybe = getByCredentialId(credentialId).stream().findAny();

    return registrationMaybe.flatMap(reg -> Optional.of(RegisteredCredential.builder()
        .credentialId(reg.getCredential().getCredentialId())
        .userHandle(reg.getUserIdentity().getId())
        .publicKeyCose(reg.getCredential().getPublicKeyCose())
        .signatureCount(reg.getCredential().getSignatureCount())
        .build()));
  }

  @Override
  public Set<RegisteredCredential> lookupAll(ByteArray credentialId) {
    return getByCredentialId(credentialId).stream().map(reg -> RegisteredCredential.builder()
        .credentialId(reg.getCredential().getCredentialId())
        .userHandle(reg.getUserIdentity().getId())
        .publicKeyCose(reg.getCredential().getPublicKeyCose())
        .signatureCount(reg.getCredential().getSignatureCount())
        .build()).collect(Collectors.toSet());
  }

  @Override
  public Boolean addRegistration(CredentialRegistration registration) {
    return credentialRepository.add(registration);
  }

  @Override
  public Collection<CredentialRegistration> getRegistrationsByUsername(String username) {
    return credentialRepository.stream()
        .filter(reg -> reg.getUserIdentity().getName().equals(username))
        .collect(Collectors.toList());
  }

  @Override
  public Collection<CredentialRegistration> getRegistrationsByUserHandle(ByteArray userHandle) {
    return credentialRepository.stream()
        .filter(reg -> reg.getUserIdentity().getId().equals(userHandle))
        .collect(Collectors.toList());
  }

  @Override
  public Collection<CredentialRegistration> getByCredentialId(ByteArray credentialId) {
    return credentialRepository.stream()
        .filter(reg -> reg.getCredential().getCredentialId().equals(credentialId))
        .filter(reg -> reg.getState().getValue().equals(StateEnum.ENABLED.getValue()))
        .collect(Collectors.toList());
  }

  @Override
  public Boolean userExists(String username) {
    return !getRegistrationsByUsername(username).isEmpty();
  }

  @Override
  public Boolean removeRegistration(ByteArray credentialId, ByteArray userHandle) {

    credentialRepository.stream()
        .filter(reg -> reg.getCredential().getCredentialId().equals(credentialId)
            && reg.getUserIdentity().getId().equals(userHandle))
        .forEach(reg -> reg.setState(StateEnum.DELETED));

    return getByCredentialId(credentialId).stream().findFirst().get().getState().getValue()
        .equals(StateEnum.DELETED.getValue());
  }

  @Override
  public Boolean updateCredentialStatus(ByteArray credentialId, ByteArray userHandle, StateEnum newState) {

    credentialRepository.stream()
        .filter(reg -> reg.getCredential().getCredentialId().equals(credentialId)
            && reg.getUserIdentity().getId().equals(userHandle))
        .forEach(reg -> {
          if (!reg.getState().stateEqual(StateEnum.DELETED)) {
            reg.setState(newState);
          }
        });

    return getByCredentialId(credentialId).stream().findFirst().get().getState().getValue()
        .equals(StateEnum.DELETED.getValue());
  }

  @Override
  public Boolean updateCredentialNickname(ByteArray credentialId, String newNickname) {
    /*
     * @TODO - Implement
     */
    return true;

  }

}
