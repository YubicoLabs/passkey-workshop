---
sidebar_position: 2
---

# Relying party implementation

This section will cover how to add attestation support to your relying party. Because the java-webauthn-server library supports attestation and the FIDO MDS out of the box, the changes are minimal. The changes should also be non-disruptive to the users who opt not to use attestation.

## Prerequisites

Ensure that you have deployed the sample found in this project. The project uses the FIDO MDS by default, and can be enabled/disabled by setting the following configuration in the `DeployProject.conf` file.

```bash
# Will denote if your application will leverage attestation
# Through the FIDO MDS
# Default: mds
# Options: mds, none
RP_ATTESTATION_TRUST_STORE=
```

[Follow the instructions on this page to deploy the application.](/docs/deploy)

## Configuration changes

First we will attempt to change the original configurations that we set for the application's `RelyingParty` changes ([this was covered in the previous section on relying party guidance](/docs/relying-party/config-and-data#rp-configurations)).

Below is the original configuration that we used to initialize our application. Note the highlighted line that contains the property `attestationTrustSource`. When we created this object, we initialized it as null.

```java {33}
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

public class RelyingPartyInstance {

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
        .attestationTrustSource(null) // Set this field to null, we will cover this in detail in the section on attestation
        .validateSignatureCounter(true)
        .build();
  }
}
```

We will extend this example to download, initialize, and globally use the FIDO MDS. First we will define a method to initialize the MDS in our application.

```java
/**
 * Helps the application to determine if it will use a metadata repository to
 * determine trusted attestation
 * Currently the only data source configured is a downloadable version of the
 * FIDO MDS
 *
 * @return an optional object that is either:
 *         A downloaded version of the FIDO MDS
 *         null indicating not to use an attestation store
 */
private FidoMetadataService resolveAttestationTrustSource() {
  /**
   * Using the env variable set by the DeployProject.conf file
   */
  String attestationTrustStoreType = System.getenv("RP_ATTESTATION_TRUST_STORE");

  /**
   * System is set to utilize the MDS
   */
  if (attestationTrustStoreType.equals("mds")) {
    try {
      /**
       * Setting needed in order to download the MDS
       * More information here:
       * https://github.com/Yubico/java-webauthn-server/tree/main/webauthn-server-attestation#:~:text=By%20default%2C,Guide%20for%20details.
       */
      System.setProperty("com.sun.security.enableCRLDP", "true");

      /**
       * Initialize helper to download the MDS
       */
      FidoMetadataDownloader downloader = FidoMetadataDownloader.builder()
          /**
           * Ensure that you have read FIDO's legal header to understand the implications
           * of using the FIDO MDS
           * More information here:
           * https://developers.yubico.com/java-webauthn-server/JavaDoc/webauthn-server-attestation/2.0.0/com/yubico/fido/metadata/FidoMetadataDownloader.FidoMetadataDownloaderBuilder.Step1.html
           */
          .expectLegalHeader(
              "Retrieval and use of this BLOB indicates acceptance of the appropriate agreement located at https://fidoalliance.org/metadata/metadata-legal-terms/")
          .useDefaultTrustRoot()
          /**
           * Cache the trust root cert and blob in a tmp folder for later use
           */
          .useTrustRootCacheFile(new File("/tmp/fido-mds-trust-root-cache.bin"))
          .useDefaultBlob()
          .useBlobCacheFile(new File("/tmp/fido-mds-blob.bin"))
          .build();

      FidoMetadataService mds = FidoMetadataService.builder()
          .useBlob(downloader.loadCachedBlob())
          .build();

      return mds;
    } catch (Exception e) {
      /**
       * There was an issue initializing the MDS
       * Opting not to use the MDS
       */
      e.printStackTrace();
      return null;
    }
  } else { // System should not utilize any MDS
    return null;
  }
}
```

Now that we have a method that initializes the MDS, we will add it to our `RelyingPartyInstance` class for use in both our `RelyingParty` configuration, and in other areas of our application. Note the highlighted lines in the sample below for the new additions to the initialization of our `RelyingPartyInstance`.

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

public class RelyingPartyInstance {

  @Getter
  private RelyingParty relyingParty;

  // highlight-start
  @Getter
  private Optional<FidoMetadataService> mds;
  // highlight-end

  @PostConstruct
  private void setRPInstance() {
    // highlight-start
    /**
     * Initialize the mds variable
     */
    FidoMetadataService initMDS = resolveAttestationTrustSource();
    this.mds = Optional.ofNullable(initMDS);
    // highlight-end

    this.relyingParty = RelyingParty.builder()
        .identity(generateIdentity())
        .credentialRepository(storageInstance.getCredentialStorage())
        .origins(generateOrigins())
        .attestationConveyancePreference(
            AttestationConveyancePreference.valueOf(System.getenv("RP_ATTESTATION_PREFERENCE")))
        .allowUntrustedAttestation(Boolean.parseBoolean(System.getenv("RP_ALLOW_UNTRUSTED_ATTESTATION")))
        // highlight-start
        /**
         * We allow this setting to be null, just in case the application does not
         * want to leverage the MDS
         * We also need to cast the object to `AttestationTrustSource`, which is the
         * Interface that is leveraged by the initMDS class `FidoMetadataService`
         */
        .attestationTrustSource(Optional.ofNullable((AttestationTrustSource) initMDS))
        // highlight-end
        .validateSignatureCounter(true)
        .build();
  }
}
```

## Registration response method changes

The registration method will need to be changed in order to attempt to use an attestation statement sent during the credential registration.

The updates will be made to the `attestationResult` method that was created in the previous topic on [relying party implementation guidance](/docs/relying-party/reg-flow#implementation-1).

The changes will be made to a method leveraged within our initial implementation, `buildCredentialDBO`. This method is used to package the passkey information to store it in our credential repository.

The original version of the method is demonstrated in the flow below.

```java
private CredentialRegistration buildCredentialDBO(PublicKeyCredentialCreationOptions request,
    RegistrationResult result) {
  return CredentialRegistration.builder()
      .userIdentity(request.getUser())
      .credentialNickname(Optional.of("My new credential"))
      .registrationTime(clock.instant())
      .lastUpdateTime(clock.instant())
      .lastUsedTime(clock.instant())
      .credential(RegisteredCredential.builder()
          .credentialId(result.getKeyId().getId())
          .userHandle(request.getUser().getId())
          .publicKeyCose(result.getPublicKeyCose())
          .signatureCount(result.getSignatureCount())
          .build())
      .iconURI(null)
      .build();
}
```

As you can note, the credential is built using either information that came directly from the passkey itself, or using "hard-coded", or default values (in the case of `credentialNickname` and `iconURI`).

In the next example we will enhance this method in order to:

- detect if attestation was sent along with the passkey
- if metadata can be inferred about the credential using the provided attestation statement

The code sample below demonstrates an updated version of the `buildCredentialDBO` method that leverages our metadata service that was initialized earlier on this page.

```java
private CredentialRegistration buildCredentialDBO(PublicKeyCredentialCreationOptions request,
    RegistrationResult result) {
  Optional<MetadataStatement> maybeMetadataEntry = resolveAttestation(result);

  String credentialName;
  String iconURI;

  /**
   * We were able to correlate the attestation statement sent by our new passkey
   * to an entry in our metadata service
   */
  if (maybeMetadataEntry.isPresent()) {
    /**
     * We will attempt to find the description and iconURI in the metadata statement
     * Note, that your entry may be missing these values, or they were not provided by the vendor
     * If this data is not present, then utilize the default values
     */
    credentialName = maybeMetadataEntry.get().getDescription().isPresent()
        ? maybeMetadataEntry.get().getDescription().get()
        : "My new passkey";
    iconURI = maybeMetadataEntry.get().getIcon().isPresent()
        ? maybeMetadataEntry.get().getIcon().get()
        : null;
  } else { // No attestation correlation made, provide our default values
    credentialName = "My new passkey";
    iconURI = null;
  }

  return CredentialRegistration.builder()
      .userIdentity(request.getUser())
      .credentialNickname(Optional.of(credentialName))
      .registrationTime(clock.instant())
      .lastUpdateTime(clock.instant())
      .lastUsedTime(clock.instant())
      .credential(RegisteredCredential.builder()
          .credentialId(result.getKeyId().getId())
          .userHandle(request.getUser().getId())
          .publicKeyCose(result.getPublicKeyCose())
          .signatureCount(result.getSignatureCount())
          .build())
      .iconURI(Optional.ofNullable(iconURI))
      .build();
}

/**
 * Determine if the registration result can resolve to a entry in the MDS
 *
 * @param result newly created passkey for the user
 * @return Optional MetadataStatement object if present
 *         null otherwise
 */
private Optional<MetadataStatement> resolveAttestation(RegistrationResult result) {
  /**
   * If the MDS was not initialized, then we can't resolve the attestation statement, so return null
   * If the result did not send attestation, then there's nothing to resolve, return null
   */
  if (relyingPartyInstance.getMds().isPresent() && result.isAttestationTrusted()) {
    /**
     * Search the MDS for the specific entry that correlates to our passkey
     */
    Set<MetadataBLOBPayloadEntry> entries = relyingPartyInstance.getMds().get().findEntries(result);

    /**
     * If an entry was found, return it
     */
    if (entries.size() != 0) {
      Optional<MetadataStatement> entry = entries.stream().findFirst()
          .flatMap(MetadataBLOBPayloadEntry::getMetadataStatement);

      return entry;
    }
    // No entry found, return null
    return Optional.ofNullable(null);
  } else {
    return Optional.ofNullable(null);
  }
}
```

## Credential repository considerations

Ensure that your credential repository is updated to reflect the metadata information that you wish to utilize or present to the user. For instance below is the table schema that is utilized for our credential repository.

```sql
CREATE TABLE credential_registrations (
  id BIGINT,
  credential TEXT, --JSON string containing the credential
  credentialid TEXT, --base64url string denoting the ID of the credential
  credential_nickname TEXT,
  last_update_time BIGINT,
  last_used_time BIGINT,
  registration_time BIGINT,
  user_handle TEXT --base64url string denoting the ID of the user
  iconURI TEXT
);
```

Note our inclusion of the `credential_nickname` and `iconURI`, which was the metadata that we captured in the previous code example.

The FIDO MDS contains more information than just authenticator name, and icon. If you wish to utilize this data, ensure that it's reflected in your table schema, and database object model/class in your Java application. Otherwise it will not be captured to be utilized later.

You could attempt to save the full `RegistrationResult` (passkey sent to finalize registration), and attempt to use it to gather attestation data at a later date.
