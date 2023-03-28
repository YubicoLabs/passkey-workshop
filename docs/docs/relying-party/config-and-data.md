---
sidebar_position: 2
---

# Data sources and RP configurations

In this section we are going to outline some essential utilities that are needed in order to perform passkey functions in our application. This will provide the foundations for our relying party application that will ensure that the requests/responses from our API are compliant with mainstream platform implementations, and that our application can interact with some form of data source.

## Data sources

To begin, let's first look at the data sources used in this project, and the interfaces that are leveraged.

### Tables

Our example will need to leverage three different tables.

- [Credential repository](/docs/architecture/relying-party#credential-repository)
- [Attestation request repository](/docs/architecture/relying-party#attestation-request-repository)
- [Assertion request repository](/docs/architecture/relying-party#assertion-request-repository)

These tables were explained in the architecture section. Please click the links above for more information.

Note, that for our implementation we are using a MySQL database. To help with extensibility, we have provided an interface that can be used to add your preferred database. The idea is that these are the standard methods you are going to need in order to facilitate passkey transactions.

### Attestation request repository

#### Database object

Below is an example of the object that is used to store data in the MySQL server,

```java
package com.yubicolabs.passkey_rp.models.dbo.mysql;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "attestation_requests")
public class AttestationOptionsDBO {
  @Getter
  @Column(columnDefinition = "text")
  String attestationRequest;

  @Getter
  @Setter
  Boolean isActive;

  @Getter
  @Column(columnDefinition = "text")
  String requestId;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;
}
```

Note that the only field that should be altered is the `isActive` property. This will indicate if this request is still live. You should invalidate this item if the request has been used, or if is past some dedicated timeout period.

The attestation request will relate to the PublicKeyCredentialCreationOptions, that will be discussed in a later section. For now just note that it's a complex object that will be stored as a JSON string.

#### Interface

Below is an example of the interface that is used to perform actions in the database.

```java
package com.yubicolabs.passkey_rp.interfaces;

import java.util.Optional;

import com.yubico.webauthn.data.PublicKeyCredentialCreationOptions;
import com.yubicolabs.passkey_rp.models.common.AttestationOptions;

public interface AttestationRequestStorage {
  /**
   * Add a new registration request to storage
   *
   * @param request created registration request to track for incoming
   *                registration
   * @return true if the registration was added to storage, and false
   *         otherwiseinterface
   */
  public Boolean insert(PublicKeyCredentialCreationOptions request, String requestId);

  /**
   * Invalidate the designated request to prevent replay attacks from duplicate
   * registrations with the same request
   *
   * @param requestID ID of the request to invalidate
   * @return true if the request was successfully invalidated, false otherwise
   */
  public Boolean invalidate(String requestID);

  /**
   * Get a registration request if it exists in storage
   *
   * @param requestID ID of the request to retrieve
   * @return Optional object that may include a registration request if found,
   *         empty otherwise
   */
  public Optional<AttestationOptions> getIfPresent(String requestID);
}
```

### Assertion request repository

#### Database object

Below is an example of the object that is used to store data in the MySQL server,

```java
package com.yubicolabs.passkey_rp.models.dbo.mysql;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "assertion_requests")
public class AssertionOptionsDBO {

  @Getter
  @Column(columnDefinition = "text")
  String assertionRequest;

  @Getter
  @Setter
  Boolean isActive;

  @Getter
  @Column(columnDefinition = "text")
  String requestId;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;
}

```

Note that the only field that should be altered is the `isActive` property. This will indicate if this request is still live. You should invalidate this item if the request has been used, or if is past some dedicated timeout period.

The assertion request will relate to the PublicKeyCredentialRequestOptions, that will be discussed in a later section. For now just note that it's a complex object that will be stored as a JSON string.

#### Interface

Below is an example of the interface that is used to perform actions in the database.

```java
package com.yubicolabs.passkey_rp.interfaces;

import java.util.Optional;

import com.yubico.webauthn.AssertionRequest;
import com.yubicolabs.passkey_rp.models.common.AssertionOptions;

public interface AssertionRequestStorage {
  /**
   * Add a new assertion request to storage
   *
   * @param request created registration request to track for incoming
   *                registration
   * @return true if the request was successfully added, false otherwise
   */
  public Boolean insert(AssertionRequest request, String requestId);

  /**
   * Invalidate the designated request to prevent replay attacks from duplicate
   * authentication with the same request
   *
   * @param requestID ID of the request to invalidate
   * @return true if the request was successfully invalidated, false otherwise
   */
  public Boolean invalidate(String requestID);

  /**
   * Get a request by ID
   *
   * @param requestID ID of the request to retrieve
   * @return Optional object that contains an assertion request if present, empty
   *         otherwise
   */
  public Optional<AssertionOptions> getIfPresent(String requestID);
}
```

### Credential repository

#### Database object

Below is an example of the object that is used to store credentials in the MySQL server,

```java
package com.yubicolabs.passkey_rp.models.dbo.mysql;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "credential_registrations")
public class CredentialRegistrationDBO {

  @Getter
  @Column(columnDefinition = "text")
  String userHandle;

  @Getter
  @Column(columnDefinition = "text")
  String credentialID;

  @Getter
  @Column(columnDefinition = "text")
  String userIdentity;

  @Getter
  @Setter
  @Column(columnDefinition = "text")
  String credentialNickname;

  @Getter
  long registrationTime;

  @Getter
  long lastUsedTime;

  @Getter
  long lastUpdateTime;

  @Getter
  @Column(columnDefinition = "text")
  String credential;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;
}
```

The property `credential` will be the field that stores the actual passkey itself. Some of the other fields like `credentialId` and `userHandle` can be inferred from the data encoded in the `credential` property, but are highlighted to help improve searchability of credentials from your application. Many operations may need to either find an individual credential by ID, or group all of the credentials by `userHandle`.

The time based properties, `registrationTime`, `lastUsedTime` and `lastUpdateTime`, are not required, and are generally best practices.

Also note how the only option that is allowed to be updated is the `credentialNickname`. As discussed in an earlier section, you should not allow users to edit the passkey (credential) directly.

#### Interface

Below is an example of the interface that is used to perform actions in the database.

```java
package com.yubicolabs.passkey_rp.interfaces;

import java.util.Collection;

import com.yubico.webauthn.CredentialRepository;
import com.yubico.webauthn.data.ByteArray;
import com.yubicolabs.passkey_rp.models.common.CredentialRegistration;;

public interface CredentialStorage extends CredentialRepository {
  /**
   * Adds a new credential for a user into the Credential Repository
   *
   * @param registration credential to be stored for the used
   * @return true indicates the credential was successfully created, false
   *         otherwise
   */
  public Boolean addRegistration(CredentialRegistration registration);

  /**
   * Gets all registrations that belong to a username
   *
   * @param username denotes the user whose credential will be returned
   * @return collection of registrations belonging to a defined username
   */
  public Collection<CredentialRegistration> getRegistrationsByUsername(String username);

  /**
   * Gets all registrations that belong to a userHandle
   *
   * @param userHandle denotes the user whose credentials will be returned
   * @return collection of registrations belonging to a defined user handle
   */
  public Collection<CredentialRegistration> getRegistrationsByUserHandle(ByteArray userHandle);

  /**
   * Gets all credentials associated to a credential ID
   *
   * @param credentialId denotes the credentialId to search for
   * @return collection of all credentials with the credential ID
   */
  public Collection<CredentialRegistration> getByCredentialId(ByteArray credentialId);

  /**
   * Determines if a user has a credential in the credential repository
   *
   * @param username denotes the user to be searched for
   * @return true if the user has a credential, false otherwise
   */
  public Boolean userExists(String username);

  /**
   * Removes a credential from the credential repository
   *
   * @param credentialId ID of the credential to be removed
   * @param userHandle   the user handle of the user attempting to make the
   *                     request
   * @return true if the credential was successfully removed, false otherwise
   */
  public Boolean removeRegistration(ByteArray credentialId, ByteArray userHandle);

  /**
   * Updates the nickname of a specific credential
   *
   * @param nickname     new nickname to be given to a credential
   * @param credentialId ID of the credential to be renamed
   * @return true if the credential was successfully renamed, false otherwise
   */
  public Boolean updateCredentialNickname(ByteArray credentialId, String newNickname);
}
```

## RP configurations

The next step is to set the common configurations for your relying party. This means setting the RP ID that is used to identify the specific relying party, the application's name, where to find the credential repository, and the web domains/origins that are allowed to create passkeys for this site.

In the java-webauthn-server, this is packaged as a `RelyingParty` object. This `RelyingParty` instance will be used through the application in order to create and process attestation and assertion requests.

The `RelyingParty` instance can be declared as such

```java
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

  @PostConstruct
  private void setRPInstance() {
    this.relyingParty = RelyingParty.builder()
        .identity(generateIdentity())
        .credentialRepository(storageInstance.getCredentialStorage())
        .origins(generateOrigins())
        .attestationConveyancePreference(
            AttestationConveyancePreference.valueOf(System.getenv("RP_ATTESTATION_PREFERENCE")))
        .allowUntrustedAttestation(Boolean.parseBoolean(System.getenv("RP_ALLOW_UNTRUSTED_ATTESTATION")))
        .validateSignatureCounter(true)
        .build();
  }

  private RelyingPartyIdentity generateIdentity() {
    /*
     * Get RP ID and Name from env variables
     */
    String rpID = System.getenv("RP_ID");
    String rpName = System.getenv("RP_NAME");

    return RelyingPartyIdentity.builder()
        .id(rpID)
        .name(rpName)
        .build();
  }

  private Set<String> generateOrigins() {
    /*
     * Get origins list value from env variables
     */

    String originsVal = System.getenv("RP_ALLOWED_ORIGINS");

    /*
     * Split the origins list by comma (as noted by the shell script)
     */

    String[] originsList = originsVal.split(",");

    /*
     * Iterate through origins list
     */

    Set<String> allowedOrigins = new HashSet<String>();

    for (int i = 0; i < originsList.length; i++) {
      allowedOrigins.add("https://" + originsList[i]);
    }

    return allowedOrigins;
  }
}

```

Note in our example that some of the fields are driven by environment variables. These variables are created during the [deployment phase](/docs/deploy). If you have been following along with this guide, then they should be utilizing default values. Later sections may change these values based on the use case, especially around the attestation related fields.

Also note the use of the `StorageInstance` reference declared at the top of the code sample. This object will contain your various data sources declared above. Behind the scenes this object is comprised of various factories that initialize, and delegate the database provider that should be used, that all adhere to the interfaces mentioned above. At this moment, you are most likely leveraging the MySQL server included in this project. For more technical information, please see the [source code](https://github.com/YubicoLabs/passkey-workshop/blob/main/examples/java-spring/src/main/java/com/yubicolabs/passkey_rp/services/storage/StorageInstance.java).
