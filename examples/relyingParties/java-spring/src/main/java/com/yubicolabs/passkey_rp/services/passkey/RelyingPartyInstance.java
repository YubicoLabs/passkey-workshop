package com.yubicolabs.passkey_rp.services.passkey;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.yubico.fido.metadata.FidoMetadataDownloader;
import com.yubico.fido.metadata.FidoMetadataService;
import com.yubico.fido.metadata.FidoMetadataService.FidoMetadataServiceBuilder;
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
  private StorageInstance storageInstance;

  @Getter
  private RelyingParty relyingParty;

  @Getter
  private Optional<FidoMetadataService> mds;

  public RelyingPartyInstance(StorageInstance storageInstance) {
    this.storageInstance = storageInstance;
  }

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
            AttestationConveyancePreference.valueOf(System.getenv("RP_ATTESTATION_PREFERENCE")))
        .allowUntrustedAttestation(Boolean.parseBoolean(System.getenv("RP_ALLOW_UNTRUSTED_ATTESTATION")))
        .attestationTrustSource(Optional.ofNullable((AttestationTrustSource) initMDS))
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
    String attestationTrustStoreType = System.getenv("RP_ATTESTATION_TRUST_STORE");

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

        FidoMetadataServiceBuilder mdsServiceBuilder = FidoMetadataService.builder()
            .useBlob(downloader.loadCachedBlob());

        /**
         * Opting to leverage an allow list to drive the allowed HA authenticators
         * 
         * A similar pattern can be used for leveraging a deny list; perform the inverse
         * filtering options against the MDS
         * 
         * The first step is to check if AAGUIDs were defined in the env file
         * If the env variable is not empty, attempt to split the string for comma
         * separated AAGUIDs
         * Next, Put each entry into an array list
         * Lastly use the arraylist to filter the MDS builder
         * 
         * An empty allow list will allow for any attestation to be used
         * A defined allow list allows a developer to explicitly define the
         * authenticators they wish the application to trust
         * 
         * Note, setting the allow list on it's own will not prevent the use of
         * untrusted attestation
         * You must also define the env variable RP_ALLOW_UNTRUSTED_ATTESTATION as false
         */
        String ALLOW_LIST_ENV = System.getenv("ALLOW_LIST_AAGUIDS");

        System.out.println("Allow list configuration: " + ALLOW_LIST_ENV);

        if (!ALLOW_LIST_ENV.equalsIgnoreCase("")) {
          String[] ALLOW_LIST_SPLIT = ALLOW_LIST_ENV.split(",");
          ArrayList<String> ALLOW_LIST_AAGUIDS = new ArrayList<String>(Arrays.asList(ALLOW_LIST_SPLIT));

          mdsServiceBuilder
              .prefilter(blobEntry -> blobEntry.getAaguid().isPresent()
                  && ALLOW_LIST_AAGUIDS.contains(blobEntry.getAaguid().get().asGuidString()));
        }

        FidoMetadataService mds = mdsServiceBuilder.build();

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
