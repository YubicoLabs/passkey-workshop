---
sidebar_position: 5
---

# Advanced Protection

This section will cover the topic of advanced protection.

Advanced protection allows a user to declare that they want their account to be secured by the highest degree of assurance regardless of other convenience mechanisms.
In this section we will outline how this is enabled from our Relying Party application.

## API definition

see `../../api/Yubico-OpenAPI3-WebAuthn-Schema-v1.yaml`

A new API method is added to retrieve (GET) or set (PUT) the advanced protection status of a user.

    /v1/user/advanced-protection/{userHandle}

GET | PUT

The user handle is supplied as a path parameter.

EXAMPLE


deploy/db.sql


database

CREATE DATABASE passkeyStorage;
CREATE TABLE
    account (
        advanced_protection BOOL DEFAULT FALSE,

CREATE TABLE
    advanced_protection_status (
        is_advanced_protection BOOL DEFAULT FALSE,

```java
  public AttestationResultResponse attestationResult(AttestationResultRequest response) throws Exception {
... }


      if (!maybeUserInAdvancedProtection.isPresent()) {
        relyingPartyInstance.getStorageInstance().getAdvancedProtectionStatusStorage().insert(
            AdvancedProtectionStatus.builder()
                .userHandle(options.getAttestationRequest().getUser().getId().getBase64Url())
                .isAdvancedProtection(false).build());
      } else {
        if (relyingPartyInstance.getStorageInstance().getAdvancedProtectionStatusStorage()
            .getIfPresent(options.getAttestationRequest().getUser().getId().getBase64Url()).get()
            .isAdvancedProtection() && !newCred.isAttestationTrusted()) {
          throw new Exception(
              "This credential cannot be registered as you are enrolled in advanced protection. All new registrations should be made using a security key");
        }
      }



  public AdvancedProtection getAdvancedProtectionStatus(String userHandle) throws Exception {
    try {      
      Optional<AdvancedProtectionStatus> maybeStatus = relyingPartyInstance.getStorageInstance()
          .getAdvancedProtectionStatusStorage().getIfPresent(userHandle);
      
      if (maybeStatus.isPresent()) {
        return AdvancedProtection.builder().userHandle(maybeStatus.get().getUserHandle())
            .enabled(maybeStatus.get().isAdvancedProtection()).build();
      } else {
        throw new Exception("This resource does not exist");
      }   
    } catch (Exception e) {
      e.printStackTrace();
      throw new Exception("There was an issue getting the advanced protection status for the user");
    }   
  }    




  public AdvancedProtection updateAdvancedProtectionStatus(String userHandle,
      UpdateAdvancedProtectionStatusRequest updateAdvancedProtectionStatusRequest) throws Exception {
    try { 
      /**
       * Check if the user is eligible for advanced protection
       */
      if (updateAdvancedProtectionStatusRequest.getEnabled()) {
        int numberOfHighAssuranceCredentials = relyingPartyInstance.getStorageInstance().getCredentialStorage()
            .getRegistrationsByUserHandle(ByteArray.fromBase64Url(userHandle)).stream()
            .filter(credential -> credential.isHighAssurance()).collect(Collectors.toList()).size();
    
        if (numberOfHighAssuranceCredentials < 2) {
          throw new Exception("The user does not qualify for advanced protection status");
        }
      } 
        
      boolean didUpdate = relyingPartyInstance.getStorageInstance().getAdvancedProtectionStatusStorage()
          .setAdvancedProtection(userHandle, updateAdvancedProtectionStatusRequest.getEnabled());
            
      if (didUpdate) {
        // No need to check optional, we know the record exists
        AdvancedProtectionStatus updated = relyingPartyInstance.getStorageInstance()
            .getAdvancedProtectionStatusStorage().getIfPresent(userHandle).get();
        /*
         * If true, go into credential registrations, and set all low assurance enabled
         * credentials as disabled
         * 
         * If false, go into credential registrations, and set all the disabled
         * credentials as enabled
         */

        updateCredentialsForAdvancedProtection(updated.getUserHandle(), updated.isAdvancedProtection());

        return AdvancedProtection.builder().userHandle(updated.getUserHandle())
            .enabled(updated.isAdvancedProtection()).build();
      } else {
        throw new Exception("The items did not update correctly");
      }
    } catch (Exception e) {
      e.printStackTrace();
      throw new Exception("There was an issue updating the advanced protection status for the user: " + e.getMessage());
    }
  }


  private void updateCredentialsForAdvancedProtection(String userHandle, Boolean isAdvancedProtection)
      throws Exception {
    Collection<CredentialRegistration> credList = relyingPartyInstance.getStorageInstance().getCredentialStorage()
        .getRegistrationsByUserHandle(ByteArray.fromBase64Url(userHandle));
    if (!credList.isEmpty()) {
      credList.forEach(credential -> {
        try {
          if (!credential.isHighAssurance() && !credential.getState().stateEqual(StateEnum.DELETED)) {
            relyingPartyInstance.getStorageInstance().getCredentialStorage()
                .updateCredentialStatus(credential.getCredential().getCredentialId(),
                    ByteArray.fromBase64Url(userHandle),
                    isAdvancedProtection ? StateEnum.DISABLED : StateEnum.ENABLED);
          }
        } catch (Exception e) {
          e.printStackTrace();
        }

      });
    } else {
      throw new Exception("This user has no credentials");
    }
  }
```


# disabling passkeys

```java
  /**
   * Method that replaces removeRegisterion
   * 
   * The registration no longer needs to be deleted by the database, and instead
   * requires that the status changes
   * A credential can have three status'
   * 
   * ENABLED - The credential is registered and can be used for authentication
   * DISABLED - The credential was deactivated when the user enrolled in advanced
   * protection. The credential can be re enabled, but for now can't be used for
   * authentication
   * DELETED - The user deleted the credential and SHOULD NOT be re-enabled
   * 
   * @param credentialId
   * @param userHandle
   * @return
   */   
  @Override 
  public Boolean updateCredentialStatus(ByteArray credentialId, ByteArray userHandle, StateEnum newState) {
    try {
      Collection<CredentialRegistration> credList = getRegistrationsByUserHandle(userHandle);

      if (!credList.isEmpty()) {
        CredentialRegistrationDBO dboItem = credentialRegistrationRepositoryMySql
            .findByCredentialID(credentialId.getBase64Url()).get(0);
  
        if (dboItem.getState().equals(StateEnum.DELETED.getValue())) {
          throw new Exception("Cannot change the state of a deleted credential");
        }
    
        dboItem.setState(newState.getValue());

        CredentialRegistrationDBO newDbo = credentialRegistrationRepositoryMySql.save(dboItem);
  
        if (newDbo.getState().equals(newState.getValue())) {
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
```
