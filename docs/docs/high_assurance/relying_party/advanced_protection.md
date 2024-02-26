---
sidebar_position: 5
---

# Advanced Protection

This section will cover the topic of advanced protection.

Advanced protection allows a user to declare that they want their account to be secured by the highest degree of assurance regardless of other convenience mechanisms.
In this section we will outline how this is enabled from our Relying Party application.

# Overview

Advanced protection is implemented as a user setting that can be enabled.
When enabled, any passkey used to authenticate to the banking application must be stored on a security key.
We need several changes to our banking application to support advanced protection, which are described below.

# API definition

A new API method `/user/advanced-protection/{userHandle}` is added to retrieve (GET) or set (PUT) the advanced protection status of a user.
The user's `userHandle` is supplied as a path parameter.

If you deployed the banking application on `localhost`, you can find its OpenAPI definition [here](http://localhost:8080/).

# Data sources

The `passkeyStorage` database needs to store the advanced protection status for every account.

This calls for a new attribute in the `account` table:

```sql
CREATE TABLE
    account (
        ...
        advanced_protection BOOL DEFAULT FALSE,
    )
```

as well as a separate `advanced_protection_status` table:

```sql
CREATE TABLE
    advanced_protection_status (
        id BIGINT NOT NULL AUTO_INCREMENT,
        user_handle NVARCHAR(256) NOT NULL,
        is_advanced_protection BOOL DEFAULT FALSE,
        PRIMARY KEY (id)
    );
```

# Registration

During registration, we need to check whether advanced protection is enabled so we can decide if a credential (i.e. a passkey) can be added.

```java
  public AttestationResultResponse attestationResult(AttestationResultRequest response) throws Exception {

  ...


      if (!maybeUserInAdvancedProtection.isPresent()) {
        relyingPartyInstance
          .getStorageInstance()
          .getAdvancedProtectionStatusStorage()
          .insert(AdvancedProtectionStatus.builder()
                .userHandle(options.getAttestationRequest().getUser().getId().getBase64Url())
                .isAdvancedProtection(false)
                .build());
      } else {
        if (relyingPartyInstance
            .getStorageInstance()
            .getAdvancedProtectionStatusStorage()
            .getIfPresent(options.getAttestationRequest().getUser().getId().getBase64Url())
            .get()
            .isAdvancedProtection() && !newCred.isAttestationTrusted()) {
          throw new Exception(
              "This credential cannot be registered as you are enrolled in advanced protection. All new registrations should be made using a security key");
        }
      }
}
```

Next, we need to introduce methods to retrieve the advanced protection status of a specific user (identified by its `userHandle`):

```java

  public AdvancedProtection getAdvancedProtectionStatus(String userHandle) throws Exception {
    try {
      Optional<AdvancedProtectionStatus> maybeStatus =
        relyingPartyInstance
          .getStorageInstance()
          .getAdvancedProtectionStatusStorage()
          .getIfPresent(userHandle);

      if (maybeStatus.isPresent()) {
        return AdvancedProtection
                .builder()
                .userHandle(maybeStatus.get().getUserHandle())
                .enabled(maybeStatus.get().isAdvancedProtection())
                .build();
      } else {
        throw new Exception("This resource does not exist");
      }
    } catch (Exception e) {
      e.printStackTrace();
      throw new Exception("There was an issue getting the advanced protection status for the user");
    }
  }
```

To update a user's advanced protection status, we first need to check wether the user is eligible for advanced protection:

```java
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
```

For any credential already registered, we need to check wether the credential is considered high assurance (i.e. stored on a security key).
If not, we need to disable the credential. Note that we do not delete the credential, as the credential can still be used when advanced protection is disabled again.

This handled by a new method `updateCredentialsForAdvancedProtection`, with a boolean parameter indicating if advanced protection is enabled (switched on)
or disabled (switched off).

```java

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

The disabling of credentials itself is implemented with a separate method `updateCredentialStatus`.

A credential can have three status'

- `ENABLED` - The credential is registered and can be used for authentication
- `DISABLED` - The credential was deactivated when the user enrolled in advanced protection. The credential can be re-enabled, but for now can't be used for authentication
- `DELETED` - The user deleted the credential and SHOULD NOT be re-enabled

```java
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
