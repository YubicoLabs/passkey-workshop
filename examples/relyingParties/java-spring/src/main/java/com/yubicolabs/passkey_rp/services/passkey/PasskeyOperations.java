package com.yubicolabs.passkey_rp.services.passkey;

import java.security.SecureRandom;
import java.time.Clock;
import java.time.ZoneOffset;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
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
import com.yubico.webauthn.AssertionRequest.AssertionRequestBuilder;
import com.yubico.webauthn.StartAssertionOptions.StartAssertionOptionsBuilder;
import com.yubico.webauthn.StartRegistrationOptions.StartRegistrationOptionsBuilder;
import com.yubico.webauthn.data.AuthenticatorAssertionResponse;
import com.yubico.webauthn.data.AuthenticatorAttachment;
import com.yubico.webauthn.data.AuthenticatorAttestationResponse;
import com.yubico.webauthn.data.AuthenticatorSelectionCriteria;
import com.yubico.webauthn.data.ByteArray;
import com.yubico.webauthn.data.ClientAssertionExtensionOutputs;
import com.yubico.webauthn.data.ClientRegistrationExtensionOutputs;
import com.yubico.webauthn.data.PublicKeyCredential;
import com.yubico.webauthn.data.PublicKeyCredentialCreationOptions;
import com.yubico.webauthn.data.ResidentKeyRequirement;
import com.yubico.webauthn.data.UserIdentity;
import com.yubico.webauthn.data.UserVerificationRequirement;
import com.yubico.webauthn.data.AuthenticatorSelectionCriteria.AuthenticatorSelectionCriteriaBuilder;
import com.yubicolabs.passkey_rp.helpers.AssertionOptionsResponseConverter;
import com.yubicolabs.passkey_rp.helpers.AttestationOptionsResponseConverter;
import com.yubicolabs.passkey_rp.models.api.AdvancedProtection;
import com.yubicolabs.passkey_rp.models.api.AssertionOptionsRequest;
import com.yubicolabs.passkey_rp.models.api.AssertionOptionsResponse;
import com.yubicolabs.passkey_rp.models.api.AssertionResultRequest;
import com.yubicolabs.passkey_rp.models.api.AssertionResultRequestAssertionResult;
import com.yubicolabs.passkey_rp.models.api.AssertionResultResponse;
import com.yubicolabs.passkey_rp.models.api.AttestationOptionsRequest;
import com.yubicolabs.passkey_rp.models.api.AttestationOptionsRequestAuthenticatorSelection;
import com.yubicolabs.passkey_rp.models.api.AttestationOptionsResponse;
import com.yubicolabs.passkey_rp.models.api.AttestationResultRequest;
import com.yubicolabs.passkey_rp.models.api.AttestationResultRequestMakeCredentialResult;
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
@Scope("singleton")
public class PasskeyOperations {

  private static final SecureRandom random = new SecureRandom();
  private final Clock clock = Clock.systemDefaultZone();

  @Autowired
  RelyingPartyInstance relyingPartyInstance;

  ObjectMapper mapper = new ObjectMapper();

  public AttestationOptionsResponse attestationOptions(AttestationOptionsRequest request) throws Exception {
    try {

      // @TODO - Add mechanism to get the UID for the user from storage (if one
      // exists)

      /**
       * See if the user exists
       */
      Optional<ByteArray> maybeUID = relyingPartyInstance.getStorageInstance().getCredentialStorage()
          .getUserHandleForUsername(request.getUserName());

      // @TODO - remove line of code that generates a fake ID for every request
      UserIdentity userIdentity = UserIdentity.builder()
          .name(request.getUserName())
          .displayName(request.getDisplayName())
          // If there is an existing user, attach their userhandle, otherwise generate a
          // new one
          .id(maybeUID.isPresent() ? maybeUID.get() : generateRandomByteArray(16))
          .build();

      /*
       * This method has been abstracted as there are a lot of "is not null" checks
       * The goal is to convert the string values into valid ENUM for the
       * AuthenticatorSelectionCriteria
       */
      AuthenticatorSelectionCriteria optionsSelectionCriteria = resolveAuthenticatorSelectionCriteria(
          request.getAuthenticatorSelection());

      StartRegistrationOptionsBuilder regOptionsBuilder = StartRegistrationOptions.builder().user(userIdentity);
      regOptionsBuilder.authenticatorSelection(optionsSelectionCriteria);
      regOptionsBuilder.timeout(180000); // 3 minutes

      if (request.getHints().isPresent()) {
        regOptionsBuilder.hints(request.getHints().get());
      }

      PublicKeyCredentialCreationOptions pkc = relyingPartyInstance.getRelyingParty()
          .startRegistration(regOptionsBuilder.build());

      ByteArray requestId = generateRandomByteArray(32);

      AttestationOptionsResponse response = AttestationOptionsResponseConverter.PKCtoResponse(pkc, requestId);

      /*
       * Insert the request into the attestation request storage to prevent replay
       * attacks
       */
      relyingPartyInstance.getStorageInstance().getAttestationRequestStorage().insert(pkc, requestId.getBase64Url());

      return response;

    } catch (Exception e) {
      e.printStackTrace();
      throw new Exception("There was an issue while generating AttestationOptions: " + e.getMessage());
    }
  }

  public AttestationResultResponse attestationResult(AttestationResultRequest response) throws Exception {
    try {
      /*
       * Check if there is an active registration request
       */
      Optional<AttestationOptions> maybeOptions = relyingPartyInstance.getStorageInstance()
          .getAttestationRequestStorage().getIfPresent(response.getRequestId());

      AttestationOptions options;

      if (maybeOptions.isPresent()) {
        options = maybeOptions.get();
      } else {
        throw new Exception("Registration request not present");
      }

      /*
       * Check if the request is still active
       */

      if (!options.getIsActive()) {
        // Not active, return error
        throw new Exception("Registration request is no longer active");
      } else {
        relyingPartyInstance.getStorageInstance().getAttestationRequestStorage().invalidate(response.getRequestId());
      }

      RegistrationResult newCred = relyingPartyInstance.getRelyingParty()
          .finishRegistration(FinishRegistrationOptions.builder()
              .request(options.getAttestationRequest())
              .response(parseRegistrationResponse(response.getMakeCredentialResult()))
              .build());

      CredentialRegistration toStore = buildCredentialDBO(options.getAttestationRequest(), newCred);

      Optional<AdvancedProtectionStatus> maybeUserInAdvancedProtection = relyingPartyInstance.getStorageInstance()
          .getAdvancedProtectionStatusStorage()
          .getIfPresent(options.getAttestationRequest().getUser().getId().getBase64Url());

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

      if (relyingPartyInstance.getStorageInstance().getCredentialStorage().addRegistration(toStore)) {
        return new AttestationResultResponse().status("created").credential(
            UserCredentialsResponseCredentialsInner.builder()
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
        throw new Exception("There was an unknown issue creating your credential");
      }

    } catch (Exception e) {
      e.printStackTrace();
      throw new Exception("There was an issue while registering your credential: " + e.getMessage());
    }
  }

  public AssertionOptionsResponse assertionOptions(AssertionOptionsRequest request) throws Exception {
    try {
      /**
       * Begin to build the PKC options
       */

      StartAssertionOptionsBuilder optionsBuilder = StartAssertionOptions.builder();

      // Configure the options with default values
      optionsBuilder.userVerification(UserVerificationRequirement.PREFERRED).timeout(180000);

      /*
       * Check if the user has an active credential stored in the DB
       */

      Collection<CredentialRegistration> credentials = relyingPartyInstance.getStorageInstance().getCredentialStorage()
          .getRegistrationsByUsername(request.getUserName()).stream()
          .filter(reg -> reg.getState().equals(StateEnum.ENABLED.getValue())).collect(Collectors.toList());

      if (credentials.size() != 0 || request.getUserName() != "") {
        /*
         * To preserve privacy, if a request was made with a non-existent username, or
         * missing a username
         * then a discoverable credentials flow will be enabled
         */

        optionsBuilder.username(request.getUserName());
      }
      if (request.getHints().isPresent()) {
        optionsBuilder.hints(request.getHints().get());
      }
      AssertionRequest pkc = relyingPartyInstance.getRelyingParty()
          .startAssertion(optionsBuilder.build());

      ByteArray requestId = generateRandomByteArray(32);

      AssertionOptionsResponse response = AssertionOptionsResponseConverter
          .PKCtoResponse(pkc.getPublicKeyCredentialRequestOptions(), requestId);

      relyingPartyInstance.getStorageInstance().getAssertionRequestStorage().insert(pkc, requestId.getBase64Url());

      return response;
    } catch (Exception e) {
      e.printStackTrace();
      throw new Exception("There was an issue while generating AssertionOptions: " + e.getMessage());
    }
  }

  public AssertionResultResponse assertionResponse(AssertionResultRequest response) throws Exception {
    try {
      /**
       * Check for assertion request
       */
      Optional<AssertionOptions> maybeOptions = relyingPartyInstance.getStorageInstance()
          .getAssertionRequestStorage().getIfPresent(response.getRequestId());

      AssertionOptions options;

      if (maybeOptions.isPresent()) {
        options = maybeOptions.get();
      } else {
        throw new Exception("Registration request not present");
      }

      /*
       * Check if the request is still active
       */

      if (!options.getIsActive()) {
        // Not active, return error
        throw new Exception("Registration request is no longer active");
      } else {
        relyingPartyInstance.getStorageInstance().getAssertionRequestStorage().invalidate(response.getRequestId());
      }

      /*
       * Build assertion request
       */
      AssertionRequestBuilder requestBuilder = AssertionRequest.builder()
          .publicKeyCredentialRequestOptions(options.getAssertionRequest().getPublicKeyCredentialRequestOptions());

      // Check if a username was present in the request
      if (options.getAssertionRequest().getUsername().isPresent()) {
        requestBuilder.username(options.getAssertionRequest().getUsername().get());
      }

      AssertionResult result = relyingPartyInstance.getRelyingParty().finishAssertion(FinishAssertionOptions.builder()
          .request(requestBuilder.build())
          .response(parseAssertionResponse(response.getAssertionResult()))
          .build());

      if (result.isSuccess()) {
        /**
         * @TODO - Do we want to include the step where the signature count is updated?
         */

        /**
         * Set loa based on credential used in result
         * 
         * Right now this is assuming all trusted attestation in loa high
         * We can refine this feature later
         */

        CredentialRegistration usedCredentialRegistration = relyingPartyInstance.getStorageInstance()
            .getCredentialStorage().getByCredentialId(result.getCredential().getCredentialId()).stream().findFirst()
            .get();

        loaEnum resultLoa = usedCredentialRegistration.isHighAssurance() ? loaEnum.HIGH : loaEnum.LOW;

        return AssertionResultResponse.builder().status("ok").loa(resultLoa).build();
      } else {
        throw new Exception("Your assertion failed for an unknown reason");
      }
    } catch (Exception e) {
      e.printStackTrace();
      throw new Exception("There was an issue finalizing your assertion your credential: " + e.getMessage());
    }

  }

  public UserCredentialsResponse getUserCredentials(String userName) throws Exception {

    try {
      // @TODO - remember to add a mechanism to verify the user making the request is
      // the same whose creds are being queried
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

  private static AuthenticatorSelectionCriteria resolveAuthenticatorSelectionCriteria(
      AttestationOptionsRequestAuthenticatorSelection request) {
    /*
     * Generate authenticator selection
     * 1. Start by inferring the values from the request, if the values were not
     * present, then select a default
     * 2. Determine if the object needs to contain an authenticatorAttachment
     */
    AuthenticatorSelectionCriteriaBuilder authSelectBuilder = AuthenticatorSelectionCriteria.builder();

    /*
     * Set the UV requirement, set to preferred if not present
     */

    if (request.getUserVerification() == null || request.getUserVerification().getValue().equals("")) {
      authSelectBuilder.userVerification(UserVerificationRequirement.PREFERRED);
    } else {
      String uvReq = request.getUserVerification().getValue().toUpperCase();
      authSelectBuilder.userVerification(UserVerificationRequirement.valueOf(uvReq));
    }

    /*
     * Set the resident key requirement, set to preferred if not present
     */
    if (request.getResidentKey() == null || request.getResidentKey().getValue().equals("")) {
      authSelectBuilder.residentKey(ResidentKeyRequirement.PREFERRED);
    } else {
      String rkReq = request.getResidentKey().getValue().toUpperCase();
      authSelectBuilder.residentKey(ResidentKeyRequirement.valueOf(rkReq));
    }

    /*
     * Set the auth attachment requirement, do not include the property if not
     * included
     */
    if (request.getAuthenticatorAttachment() != null) {
      String aaReq = request.getAuthenticatorAttachment().getValue();
      if (aaReq.equals("cross-platform")) {
        authSelectBuilder.authenticatorAttachment(AuthenticatorAttachment.CROSS_PLATFORM);
      } else if (aaReq.equals("platform")) {
        authSelectBuilder.authenticatorAttachment(AuthenticatorAttachment.PLATFORM);
      }
    }

    return authSelectBuilder.build();
  }

  private PublicKeyCredential<AuthenticatorAttestationResponse, ClientRegistrationExtensionOutputs> parseRegistrationResponse(
      AttestationResultRequestMakeCredentialResult response) throws Exception {
    try {
      String responseJSON = mapper.writeValueAsString(response);

      return PublicKeyCredential
          .parseRegistrationResponseJson(responseJSON);

    } catch (Exception e) {
      throw new Exception("There was an issue parsing the credential: " + e.getMessage());
    }
  }

  private PublicKeyCredential<AuthenticatorAssertionResponse, ClientAssertionExtensionOutputs> parseAssertionResponse(
      AssertionResultRequestAssertionResult response) throws Exception {
    try {
      String responseJSON = mapper.writeValueAsString(response);

      return PublicKeyCredential
          .parseAssertionResponseJson(responseJSON);

    } catch (Exception e) {
      throw new Exception("There was an issue parsing the credential: " + e.getMessage());
    }
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
