package com.yubicolabs.passkey_rp.services.passkey;

import java.io.File;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.yubico.fido.metadata.FidoMetadataDownloader;
import com.yubico.fido.metadata.FidoMetadataService;
import com.yubico.fido.metadata.MetadataBLOB;
import com.yubico.webauthn.RelyingParty;
import com.yubico.webauthn.attestation.AttestationTrustSource;
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

  @Autowired
  private Environment env;
  @Getter
  private Optional<FidoMetadataService> mds;

  // @TODO - Read these value from ENV

  @PostConstruct
  private void setRPInstance() {

    /**
     * Initialize the mds variable
     */

    FidoMetadataService initMDS = resolveAttestationTrustSource();
    this.mds = Optional.ofNullable(initMDS);

    this.relyingParty = RelyingParty.builder()
        .identity(generateIdentity())
        .credentialRepository(storageInstance.getCredentialStorage())
        .origins(generateOrigins())
        .attestationConveyancePreference(
            AttestationConveyancePreference.valueOf(env.getProperty("RP_ATTESTATION_PREFERENCE")))
        .allowUntrustedAttestation(Boolean.parseBoolean(env.getProperty("RP_ALLOW_UNTRUSTED_ATTESTATION")))
        .allowUntrustedAttestation(Boolean.parseBoolean(env.getProperty("RP_ALLOW_UNTRUSTED_ATTESTATION")))
        .attestationTrustSource(Optional.ofNullable((AttestationTrustSource) initMDS))
        .validateSignatureCounter(true)
        .build();
  }

  private RelyingPartyIdentity generateIdentity() {
    /*
     * Get RP ID and Name from env variables
     */
    String rpID = env.getProperty("RP_ID");
    String rpName = env.getProperty("RP_NAME");

    return RelyingPartyIdentity.builder()
        .id(rpID)
        .name(rpName)
        .build();
  }

  private Set<String> generateOrigins() {
    /*
     * Get origins list value from env variables
     */

    String originsVal = env.getProperty("RP_ALLOWED_ORIGINS");

    /*
     * Split the origins list by comma (as noted by the shell script)
     */

    String[] originsList = originsVal.split(",");

    /*
     * Iterate through origins list
     */

    Set<String> allowedOrigins = new HashSet<String>();

    for (int i = 0; i < originsList.length; i++) {
      allowedOrigins.add("http://" + originsList[i]);
      allowedOrigins.add("https://" + originsList[i]);
    }

    return allowedOrigins;
  }

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
    String attestationTrustStoreType = env.getProperty("RP_ATTESTATION_TRUST_STORE");

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
         * Read
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
        e.printStackTrace();
        System.out.println("There was an issue resolving the FIDO MDS");
        System.out.println("Opting to continue without the use of the MDS");

        return null;
      }
    } else {
      return null;
    }
  }

}
