package com.yubicolabs.passkey_rp.services.passkey;

import java.security.SecureRandom;
import java.time.Clock;
import java.time.ZoneOffset;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yubico.fido.metadata.MetadataBLOBPayloadEntry;
import com.yubico.fido.metadata.MetadataStatement;
import com.yubico.webauthn.AssertionRequest;
import com.yubico.webauthn.AssertionResult;
import com.yubico.webauthn.FinishAssertionOptions;
import com.yubico.webauthn.FinishRegistrationOptions;
import com.yubico.webauthn.RegisteredCredential;
import com.yubico.webauthn.RegistrationResult;
import com.yubico.webauthn.StartAssertionOptions;
import com.yubico.webauthn.StartRegistrationOptions;
import com.yubico.webauthn.data.ByteArray;
import com.yubico.webauthn.data.PublicKeyCredentialCreationOptions;
import com.yubico.webauthn.data.PublicKeyCredentialRequestOptions;
import com.yubico.webauthn.data.UserIdentity;
import com.yubico.webauthn.data.UserVerificationRequirement;
import com.yubicolabs.passkey_rp.interfaces.AdvancedProtectionStatusStorage;
import com.yubicolabs.passkey_rp.models.api.AdvancedProtection;
import com.yubicolabs.passkey_rp.models.api.AssertionOptionsRequest;
import com.yubicolabs.passkey_rp.models.api.AssertionOptionsResponse;
import com.yubicolabs.passkey_rp.models.api.AssertionResultRequest;
import com.yubicolabs.passkey_rp.models.api.AssertionResultResponse;
import com.yubicolabs.passkey_rp.models.api.AttestationOptionsRequest;
import com.yubicolabs.passkey_rp.models.api.AttestationOptionsResponse;
import com.yubicolabs.passkey_rp.models.api.AttestationResultRequest;
import com.yubicolabs.passkey_rp.models.api.AttestationResultResponse;
import com.yubicolabs.passkey_rp.models.api.UpdateAdvancedProtectionStatusRequest;
import com.yubicolabs.passkey_rp.models.api.UserCredentialDelete;
import com.yubicolabs.passkey_rp.models.api.UserCredentialDeleteResponse;
import com.yubicolabs.passkey_rp.models.api.UserCredentialUpdate;
import com.yubicolabs.passkey_rp.models.api.UserCredentialUpdateResponse;
import com.yubicolabs.passkey_rp.models.api.UserCredentialsResponse;
import com.yubicolabs.passkey_rp.models.api.UserCredentialsResponseCredentialsInner;
import com.yubicolabs.passkey_rp.models.api.AssertionResultResponse.loaEnum;
import com.yubicolabs.passkey_rp.models.common.AdvancedProtectionStatus;
import com.yubicolabs.passkey_rp.models.common.AssertionOptions;
import com.yubicolabs.passkey_rp.models.common.AttestationOptions;
import com.yubicolabs.passkey_rp.models.common.CredentialRegistration;
import com.yubicolabs.passkey_rp.models.common.CredentialRegistration.StateEnum;

@Service
public class PasskeyOperations {

  private static final SecureRandom random = new SecureRandom();
  private final Clock clock = Clock.systemDefaultZone();

  RelyingPartyInstance relyingPartyInstance;

  ObjectMapper mapper = new ObjectMapper();

  public PasskeyOperations(RelyingPartyInstance relyingPartyInstance) {
    this.relyingPartyInstance = relyingPartyInstance;
  }

  public AttestationOptionsResponse attestationOptions(AttestationOptionsRequest request) {
    /**
     * See if the user has an existing credential in the DB
     */
    Optional<ByteArray> maybeUID = relyingPartyInstance.getStorageInstance().getCredentialStorage()
        .getUserHandleForUsername(request.getUserName());

    /*
     * Build a user identity object
     * 
     * If user is established, attach the ID, if not then generate a new UID
     */
    UserIdentity userIdentity = UserIdentity.builder()
        .name(request.getUserName())
        .displayName(request.getDisplayName())
        // If there is an existing user, attach their userhandle, otherwise generate a
        // new one
        .id(maybeUID.isPresent() ? maybeUID.get() : generateRandomByteArray(16))
        .build();

    PublicKeyCredentialCreationOptions pkc = relyingPartyInstance.getRelyingParty()
        .startRegistration(StartRegistrationOptions.builder()
            .user(userIdentity)
            .authenticatorSelection(request.getAuthenticatorSelection())
            .timeout(180000)
            .hints(request.getHints().orElseGet(() -> new String[0])).build());

    ByteArray requestId = generateRandomByteArray(32);

    relyingPartyInstance.getStorageInstance().getAttestationRequestStorage().insert(pkc, requestId.getBase64Url());

    return AttestationOptionsResponse.builder().requestId(requestId.getBase64Url()).publicKey(pkc).build();
  }

  public AttestationResultResponse attestationResult(AttestationResultRequest response) {
    /*
     * Check if there is an active registration request
     */
    Optional<AttestationOptions> maybeOptions = relyingPartyInstance.getStorageInstance()
        .getAttestationRequestStorage().getIfPresent(response.getRequestId());

    AttestationOptions options;

    if (maybeOptions.isPresent()) {
      options = maybeOptions.get();
    } else {
      throw new RuntimeException("This is not a valid registration requestId");
    }

    /*
     * Check if the request is still active
     */

    if (!options.getIsActive()) {
      // Not active, return error
      throw new RuntimeException("Registration request is no longer active");
    } else {
      relyingPartyInstance.getStorageInstance().getAttestationRequestStorage().invalidate(response.getRequestId());
    }

    RegistrationResult newCredential;

    try {
      newCredential = relyingPartyInstance.getRelyingParty()
          .finishRegistration(FinishRegistrationOptions.builder()
              .request(options.getAttestationRequest())
              .response(response.getMakeCredentialResult())
              .build());
    } catch (Exception e) {
      e.printStackTrace();
      throw new RuntimeException(e.getMessage());
    }

    // Store credential in database
    CredentialRegistration toStore = buildCredentialDBO(options.getAttestationRequest(), newCredential);

    /*
     * Actions to support adding credentials to a user with advanced protection
     * enabled
     */

    /*
     * Check if user has a profile in the advanced protection DB
     * 
     * If no - Add the user (this is due to the user previously not having a
     * credential)
     * 
     * If yes, then check the user's AP status
     * - If AP is enabled, then the new credential needs to have come from a high
     * assurance authenticator
     */
    AdvancedProtectionStatusStorage advancedProtectionStatusStorage = relyingPartyInstance.getStorageInstance()
        .getAdvancedProtectionStatusStorage();

    String userHandle = options.getAttestationRequest().getUser().getId().getBase64Url();

    Optional<AdvancedProtectionStatus> maybeUserInAdvancedProtection = advancedProtectionStatusStorage
        .getIfPresent(userHandle);

    if (!maybeUserInAdvancedProtection.isPresent()) {
      advancedProtectionStatusStorage.insert(
          AdvancedProtectionStatus.builder()
              .userHandle(userHandle)
              .isAdvancedProtection(false).build());
    } else {
      if (maybeUserInAdvancedProtection.get().isAdvancedProtection() && !newCredential.isAttestationTrusted()) {
        throw new RuntimeException(
            "This credential cannot be registered as you are enrolled in advanced protection. All new registrations should be made using a security key");
      }
    }

    // Attempt to add the credential to the DB
    if (relyingPartyInstance.getStorageInstance().getCredentialStorage().addRegistration(toStore)) {
      return new AttestationResultResponse().status("created")
          .credential(UserCredentialsResponseCredentialsInner.builder()
              .id(toStore.getCredential().getCredentialId().getBase64Url())
              .type("public-key")
              .nickName(toStore.getCredentialNickname().get())
              .registrationTime(toStore.getRegistrationTime().atOffset(ZoneOffset.UTC))
              .lastUsedTime(toStore.getLastUsedTime().atOffset(ZoneOffset.UTC))
              .iconURI((toStore.getIconURI().isPresent() ? toStore.getIconURI().get() : null))
              .isHighAssurance(toStore.isHighAssurance())
              .state(toStore.getState().getValue())
              .build());
    } else {
      throw new RuntimeException("There was an unknown error when saving the credential");
    }
  }

  public AssertionOptionsResponse assertionOptions(AssertionOptionsRequest request) {

    StartAssertionOptions options = StartAssertionOptions.builder()
        .userVerification(UserVerificationRequirement.PREFERRED)
        .timeout(180000)
        .username(Optional.ofNullable(request.getUserName()))
        .hints(request.getHints().orElseGet(() -> new String[0]))
        .build();

    AssertionRequest assertionRequest = relyingPartyInstance.getRelyingParty().startAssertion(options);

    PublicKeyCredentialRequestOptions pkc = assertionRequest.getPublicKeyCredentialRequestOptions();

    ByteArray requestId = generateRandomByteArray(32);

    relyingPartyInstance.getStorageInstance().getAssertionRequestStorage().insert(
        assertionRequest, requestId.getBase64Url());

    return AssertionOptionsResponse.builder()
        .requestId(requestId.getBase64Url())
        .publicKey(pkc)
        .build();
  }

  public AssertionResultResponse assertionResponse(AssertionResultRequest response) {
    /**
     * Check for assertion request
     */
    Optional<AssertionOptions> maybeOptions = relyingPartyInstance.getStorageInstance()
        .getAssertionRequestStorage().getIfPresent(response.getRequestId());

    if (!maybeOptions.isPresent()) {
      throw new RuntimeException("Registration request not present");
    }

    AssertionOptions options = maybeOptions.get();

    /*
     * Check if the request is still active
     */

    if (!options.getIsActive()) {
      // Not active, return error
      throw new RuntimeException("Registration request is no longer active");
    }

    // Consume request, make it invalid to replays
    relyingPartyInstance.getStorageInstance().getAssertionRequestStorage().invalidate(response.getRequestId());

    AssertionResult result;

    Optional<String> maybeUsername = options.getAssertionRequest().getUsername();

    // Null username in request is saved as "", which causes a mismatch with the
    // final eval
    // If request username is empty, then set the value to null for processing
    if (maybeUsername.isPresent() && maybeUsername.get().equals("")) {
      maybeUsername = Optional.ofNullable(null);
    }

    try {
      result = relyingPartyInstance.getRelyingParty().finishAssertion(FinishAssertionOptions.builder()
          .request(AssertionRequest.builder()
              .publicKeyCredentialRequestOptions(options.getAssertionRequest().getPublicKeyCredentialRequestOptions())
              .username(maybeUsername)
              .build())
          .response(response.getAssertionResult()).build());
    } catch (Exception e) {
      e.printStackTrace();
      throw new RuntimeException(e.getMessage());
    }

    // Throw an error if the assertion validation failed
    if (!result.isSuccess()) {
      throw new RuntimeException("The assertion validation failed. Please try again");
    }

    /**
     * Set loa (level of assurance) based on credential used in result
     * 
     * Right now this is assuming all trusted attestation in loa high
     * We can refine this feature later
     */

    CredentialRegistration usedCredentialRegistration = relyingPartyInstance.getStorageInstance()
        .getCredentialStorage().getByCredentialId(result.getCredential().getCredentialId()).stream().findFirst()
        .get();

    loaEnum resultLoa = usedCredentialRegistration.isHighAssurance() ? loaEnum.HIGH : loaEnum.LOW;

    return AssertionResultResponse.builder().status("ok").loa(resultLoa).build();
  }

  public UserCredentialsResponse getUserCredentials(String userName) throws Exception {
    try {
      Collection<CredentialRegistration> credentials = relyingPartyInstance.getStorageInstance().getCredentialStorage()
          .getRegistrationsByUsername(userName).stream()
          .filter(reg -> reg.getState().getValue().equals(StateEnum.ENABLED.getValue())).collect(Collectors.toList());

      List<UserCredentialsResponseCredentialsInner> credList = credentials.stream()
          .map(cred -> UserCredentialsResponseCredentialsInner.builder()
              .id(cred.getCredential().getCredentialId().getBase64Url())
              .type("public-key")
              .nickName(cred.getCredentialNickname().get())
              .registrationTime(cred.getRegistrationTime().atOffset(ZoneOffset.UTC))
              .lastUsedTime(cred.getLastUsedTime().atOffset(ZoneOffset.UTC))
              .iconURI((cred.getIconURI().isPresent() ? cred.getIconURI().get() : null))
              .isHighAssurance(cred.isHighAssurance())
              .state(cred.getState().getValue())
              .build())
          .collect(Collectors.toList());

      return new UserCredentialsResponse().credentials(credList);
    } catch (Exception e) {
      e.printStackTrace();
      throw new Exception("There was an issue getting your credentials: " + e.getMessage());
    }

  }

  private static ByteArray generateRandomByteArray(int length) {
    byte[] bytes = new byte[length];
    random.nextBytes(bytes);
    return new ByteArray(bytes);
  }

  private CredentialRegistration buildCredentialDBO(PublicKeyCredentialCreationOptions request,
      RegistrationResult result) {
    Optional<MetadataStatement> maybeMetadataEntry = resolveAttestation(result);

    String credentialName;
    String iconURI;
    boolean isHighAssurance;

    if (maybeMetadataEntry.isPresent()) {
      credentialName = maybeMetadataEntry.get().getDescription().isPresent()
          ? maybeMetadataEntry.get().getDescription().get()
          : "My new passkey";
      iconURI = maybeMetadataEntry.get().getIcon().isPresent()
          ? maybeMetadataEntry.get().getIcon().get()
          : null;
      isHighAssurance = true;
    } else {
      credentialName = "My new passkey";
      iconURI = null;
      isHighAssurance = false;
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
        .isHighAssurance(isHighAssurance)
        .state(StateEnum.ENABLED)
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
    if (relyingPartyInstance.getMds().isPresent() && result.isAttestationTrusted()) {
      Set<MetadataBLOBPayloadEntry> entries = relyingPartyInstance.getMds().get().findEntries(result);

      if (entries.size() != 0) {
        Optional<MetadataStatement> entry = entries.stream().findFirst()
            .flatMap(MetadataBLOBPayloadEntry::getMetadataStatement);

        return entry;
      }
      return Optional.ofNullable(null);
    } else {
      return Optional.ofNullable(null);
    }
  }

  public UserCredentialDeleteResponse deleteCredential(UserCredentialDelete credential) throws Exception {
    try {
      Collection<CredentialRegistration> credentials = relyingPartyInstance.getStorageInstance().getCredentialStorage()
          .getByCredentialId(ByteArray.fromBase64Url(credential.getId()));

      if (credentials.size() == 0) {
        throw new Exception("This credential does not exists");
      } else {
        CredentialRegistration deleteCred = credentials.stream().findFirst().get();

        Boolean deleteResult = relyingPartyInstance.getStorageInstance().getCredentialStorage().removeRegistration(
            deleteCred.getCredential().getCredentialId(), deleteCred.getCredential().getUserHandle());
        if (deleteResult) {
          UserCredentialDeleteResponse response = UserCredentialDeleteResponse.builder().result("deleted").build();
          return response;
        } else {
          throw new Exception("There was an unknown issue deleting the credential");
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
      throw new Exception("There was an issue deleting your credential" + e.getMessage());
    }
  }

  public UserCredentialUpdateResponse updateCredentialNickname(UserCredentialUpdate userCredentialUpdate)
      throws Exception {
    try {
      Collection<CredentialRegistration> credentials = relyingPartyInstance.getStorageInstance().getCredentialStorage()
          .getByCredentialId(ByteArray.fromBase64Url(userCredentialUpdate.getId()));

      if (credentials.size() == 0) {
        throw new Exception("This credential does not exist");
      } else {
        CredentialRegistration updateCred = credentials.stream().findFirst().get();

        Boolean updateResult = relyingPartyInstance.getStorageInstance().getCredentialStorage()
            .updateCredentialNickname(
                updateCred.getCredential().getCredentialId(), userCredentialUpdate.getNickName());

        if (updateResult) {
          return UserCredentialUpdateResponse.builder().status("updated").build();

        } else {
          throw new Exception("There was an unknown issue deleting the credential");
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
      throw new Exception("There was an issue updating your credentials nickname: " + e.getMessage());
    }
  }

  public AdvancedProtection getAdvancedProtectionStatus(String userHandle) throws Exception {
    try {
      /**
       * Check if the user exists
       * If not, throw error
       */

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

}
