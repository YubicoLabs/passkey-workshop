package com.yubicolabs.passkey_rp.services.storage.mysql;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yubico.webauthn.RegisteredCredential;
import com.yubico.webauthn.RegistrationResult;
import com.yubico.webauthn.data.ByteArray;
import com.yubico.webauthn.data.PublicKeyCredentialDescriptor;
import com.yubico.webauthn.data.UserIdentity;
import com.yubicolabs.passkey_rp.interfaces.CredentialStorage;
import com.yubicolabs.passkey_rp.models.common.CredentialRegistration;
import com.yubicolabs.passkey_rp.models.dbo.mysql.CredentialRegistrationDBO;

@Component
public class CredentialRegistrationStorage_mysql implements CredentialStorage {

  @Autowired(required = false)
  private CredentialRegistrationRepositoryMySql credentialRegistrationRepositoryMySql;

  ObjectMapper mapper = new ObjectMapper();

  @Override
  public Set<PublicKeyCredentialDescriptor> getCredentialIdsForUsername(String username) {
    return getRegistrationsByUsername(username)
        .stream()
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
    try {
      CredentialRegistrationDBO newItem = CredentialRegistrationDBO.builder()
          .userHandle(registration.getUserIdentity().getId().getBase64Url())
          .credentialID(registration.getCredential().getCredentialId().getBase64Url())
          .userIdentity(mapper.writeValueAsString(registration.getUserIdentity()))
          .credentialNickname(
              registration.getCredentialNickname().isPresent() ? registration.getCredentialNickname().get() : "")
          .registrationTime(registration.getRegistrationTime().toEpochMilli())
          .lastUpdateTime(registration.getLastUpdateTime().toEpochMilli())
          .lastUsedTime(registration.getLastUsedTime().toEpochMilli())
          .credential(mapper.writeValueAsString(registration.getCredential()))
          .iconURI(registration.getIconURI().isPresent() ? registration.getIconURI().get() : null)
          .isHighAssurance(registration.isHighAssurance())
          .build();

      credentialRegistrationRepositoryMySql.save(newItem);
      return true;
    } catch (Exception e) {
      System.out.println("There was an issue saving your registration");
      e.printStackTrace();
      return false;
    }
  }

  @Override
  public Collection<CredentialRegistration> getRegistrationsByUsername(String username) {

    Collection<CredentialRegistrationDBO> credRegList = credentialRegistrationRepositoryMySql.findAll();

    return credRegList.stream().filter(reg -> usernameToDBOUserIdentity(reg.getUserIdentity(), username))
        .map(regDBO -> buildCredentialRegistration(regDBO))
        .collect(Collectors.toList());
  }

  @Override
  public Collection<CredentialRegistration> getRegistrationsByUserHandle(ByteArray userHandle) {
    try {
      Collection<CredentialRegistrationDBO> credRegList = credentialRegistrationRepositoryMySql
          .findByUserHandle(userHandle.getBase64Url());

      return credRegList.stream().map(regDBO -> buildCredentialRegistration(regDBO)).collect(Collectors.toList());

    } catch (Exception e) {
      System.out.println("There was an issue generating a list of credentials");
      e.printStackTrace();
      return new ArrayList<CredentialRegistration>();
    }
  }

  private Boolean usernameToDBOUserIdentity(String dboUI, String username) {
    try {
      UserIdentity ui = mapper.readValue(dboUI, UserIdentity.class);

      if (username.equals(ui.getName())) {
        return true;
      } else {
        return false;
      }
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
  }

  private CredentialRegistration buildCredentialRegistration(CredentialRegistrationDBO regDBO) {
    try {
      return CredentialRegistration.builder()
          .userIdentity(mapper.readValue(regDBO.getUserIdentity(), UserIdentity.class))
          .credentialNickname(Optional
              .ofNullable(regDBO.getCredentialNickname().equalsIgnoreCase("") ? null : regDBO.getCredentialNickname()))
          .registrationTime(Instant.ofEpochMilli(regDBO.getRegistrationTime()))
          .lastUsedTime(Instant.ofEpochMilli(regDBO.getLastUpdateTime()))
          .lastUpdateTime(Instant.ofEpochMilli(regDBO.getLastUpdateTime()))
          .credential(mapper.readValue(regDBO.getCredential(), RegisteredCredential.class))
          .iconURI(Optional.ofNullable(regDBO.getIconURI()))
          .isHighAssurance(regDBO.isHighAssurance())
          .build();
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  @Override
  public Collection<CredentialRegistration> getByCredentialId(ByteArray credentialId) {
    Collection<CredentialRegistrationDBO> credList = credentialRegistrationRepositoryMySql
        .findByCredentialID(credentialId.getBase64Url());

    return credList.stream().map(regDBO -> buildCredentialRegistration(regDBO)).collect(Collectors.toList());
  }

  @Override
  public Boolean userExists(String username) {
    return !getRegistrationsByUsername(username).isEmpty();
  }

  @Override
  public Boolean removeRegistration(ByteArray credentialId, ByteArray userHandle) {
    Collection<CredentialRegistration> credListByID = getByCredentialId(credentialId);

    /*
     * Check if there are any items in the repository
     */
    if (!credListByID.isEmpty()) {
      Collection<CredentialRegistration> credListByIDandUH = credListByID.stream()
          .filter(reg -> reg.getUserIdentity().getId().equals(userHandle)).collect(Collectors.toList());

      /*
       * Check if the credential ID belongs to the userhandle
       */
      if (!credListByIDandUH.isEmpty()) {
        Long numRecordsDeleted = credentialRegistrationRepositoryMySql
            .deleteByCredentialID(credentialId.getBase64Url());

        /*
         * Only one record should be removed - If this returns false then something
         * should be done
         */
        return numRecordsDeleted == 1;
      } else {
        System.out.println("The credentialID and userhandle are not associated");
        return false;
      }
    } else {
      System.out.println("No items were found by the provided credentialID");
      return false;
    }
  }

  @Override
  public Boolean updateCredentialNickname(ByteArray credentialId, String newNickname) {
    try {
      Collection<CredentialRegistration> credList = getByCredentialId(credentialId);

      if (!credList.isEmpty()) {
        CredentialRegistrationDBO dboObj = credentialRegistrationRepositoryMySql
            .findByCredentialID(credentialId.getBase64Url()).get(0);
        dboObj.setCredentialNickname(newNickname);

        CredentialRegistrationDBO newObj = credentialRegistrationRepositoryMySql.save(dboObj);

        System.out.println(newObj.getCredentialID() + " ****** " + newObj.getCredentialNickname());

        if (newObj.getCredentialNickname().equals(newNickname)) {
          return true;
        } else {
          return false;
        }
      } else {
        return false;
      }
    } catch (Exception e) {
      e.printStackTrace();
      System.out.println("There was a failure updating the id");
      return false;
    }
  }
}
