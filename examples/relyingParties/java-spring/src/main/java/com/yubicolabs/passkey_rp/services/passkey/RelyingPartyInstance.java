package com.yubicolabs.passkey_rp.services.passkey;

import java.io.File;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
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

  // @TODO - Read these value from ENV

  @PostConstruct
  private void setRPInstance() {

    this.relyingParty = RelyingParty.builder()
        .identity(generateIdentity())
        .credentialRepository(storageInstance.getCredentialStorage())
        .origins(generateOrigins())
        .attestationConveyancePreference(
            AttestationConveyancePreference.valueOf(System.getenv("RP_ATTESTATION_PREFERENCE")))
        .allowUntrustedAttestation(Boolean.parseBoolean(System.getenv("RP_ALLOW_UNTRUSTED_ATTESTATION")))
        .attestationTrustSource(resolveAttestationTrustSource())
        .validateSignatureCounter(true)
        .build();

    System.out.println("Size of origins list: " + this.relyingParty.getOrigins().size());
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

  private Optional<AttestationTrustSource> resolveAttestationTrustSource() {
    String attestationTrustStoreType = System.getenv("RP_ATTESTATION_TRUST_STORE");

    if (attestationTrustStoreType.equals("mds")) {
      try {
        System.setProperty("com.sun.security.enableCRLDP", "true");
        FidoMetadataDownloader downloader = FidoMetadataDownloader.builder()
            .expectLegalHeader(
                "Retrieval and use of this BLOB indicates acceptance of the appropriate agreement located at https://fidoalliance.org/metadata/metadata-legal-terms/")
            .useDefaultTrustRoot()
            .useTrustRootCacheFile(new File("/tmp/fido-mds-trust-root-cache.bin"))
            .useDefaultBlob()
            .useBlobCacheFile(new File("/tmp/fido-mds-blob.bin"))
            .build();

        FidoMetadataService mds = FidoMetadataService.builder()
            .useBlob(downloader.loadCachedBlob())
            .build();

        return Optional.ofNullable(mds);
      } catch (Exception e) {
        e.printStackTrace();
        System.out.println("There was an issue resolving the FIDO MDS");
        System.out.println("Opting to continue without the use of the MDS");

        return Optional.ofNullable(null);
      }
    } else {
      return Optional.ofNullable(null);
    }
  }

}
