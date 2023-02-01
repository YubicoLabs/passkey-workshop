package com.yubicolabs.passkey_rp.services.passkey;

import java.security.SecureRandom;
import java.time.Clock;
import java.time.ZoneOffset;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import com.yubicolabs.passkey_rp.models.api.UserCredentialsResponse;
import com.yubicolabs.passkey_rp.models.api.UserCredentialsResponseCredentialsInner;
import com.yubicolabs.passkey_rp.models.dbo.AssertionOptionsDBO;
import com.yubicolabs.passkey_rp.models.dbo.AttestationOptionsDBO;
import com.yubicolabs.passkey_rp.models.dbo.CredentialRegistration;

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
          .id(maybeUID.isPresent() ? maybeUID.get() : generateRandomByteArray(32))
          .build();

      /*
       * This method has been abstracted as there are a lot of "is not null" checks
       * The goal is to convert the string values into valid ENUM for the
       * AuthenticatorSelectionCriteria
       */
      AuthenticatorSelectionCriteria optionsSelectionCriteria = resolveAuthenticatorSelectionCriteria(
          request.getAuthenticatorSelection());

      PublicKeyCredentialCreationOptions pkc = relyingPartyInstance.getRelyingParty()
          .startRegistration(StartRegistrationOptions.builder()
              .user(userIdentity)
              .authenticatorSelection(optionsSelectionCriteria)
              .timeout(180000) // 3 minutes
              .build());

      ByteArray requestId = generateRandomByteArray(32);

      AttestationOptionsResponse response = AttestationOptionsResponseConverter.PKCtoResponse(pkc, requestId);

      /*
       * Insert the request into the attestation request storage to prevent replay
       * attacks
       */
      relyingPartyInstance.getStorageInstance().getAttestationRequestStorage().insert(pkc, requestId.getBase64Url());

      return response;

    } catch (Exception e) {
      System.out.println(e.getMessage());
      throw new Exception("There was an issue while generating AttestationOptions: " + e.getMessage());
    }
  }

  public AttestationResultResponse attestationResult(AttestationResultRequest response) throws Exception {
    try {
      /*
       * Check if there is an active registration request
       */
      Optional<AttestationOptionsDBO> maybeOptions = relyingPartyInstance.getStorageInstance()
          .getAttestationRequestStorage().getIfPresent(response.getRequestId());

      AttestationOptionsDBO options;

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
              .request(options.getPkc())
              .response(parseRegistrationResponse(response.getMakeCredentialResult()))
              .build());

      CredentialRegistration toStore = buildCredentialDBO(options.getPkc(), newCred);

      if (relyingPartyInstance.getStorageInstance().getCredentialStorage().addRegistration(toStore)) {
        return new AttestationResultResponse().status("created");
      } else {
        throw new Exception("There was an issue creating your credential");
      }

    } catch (Exception e) {
      System.out.println(e.getMessage());
      throw new Exception(e.getMessage());
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
       * Check if the user has a credential stored in the DB
       */

      Collection<CredentialRegistration> credentials = relyingPartyInstance.getStorageInstance().getCredentialStorage()
          .getRegistrationsByUsername(request.getUserName());

      if (credentials.size() != 0 || request.getUserName() != "") {
        /*
         * To preserve privacy, if a request was made with a non-existent username, or
         * missing a username
         * then a discoverable credentials flow will be enabled
         */

        optionsBuilder.username(request.getUserName());
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
      Optional<AssertionOptionsDBO> maybeOptions = relyingPartyInstance.getStorageInstance()
          .getAssertionRequestStorage().getIfPresent(response.getRequestId());

      AssertionOptionsDBO options;

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

        return AssertionResultResponse.builder().status("ok").build();
      } else {
        throw new Exception("Invalid assertion");
      }
    } catch (Exception e) {
      e.printStackTrace();
      throw new Exception("There was an issue while generating AssertionOptions: " + e.getMessage());
    }

  }

  public UserCredentialsResponse getUserCredentials(String userName) throws Exception {

    try {
      // @TODO - remember to add a mechanism to verify the user making the request is
      // the same whose creds are being queried
      Collection<CredentialRegistration> credentials = relyingPartyInstance.getStorageInstance().getCredentialStorage()
          .getRegistrationsByUsername(userName);

      List<UserCredentialsResponseCredentialsInner> credList = credentials.stream()
          .map(cred -> UserCredentialsResponseCredentialsInner.builder()
              .id(cred.getCredential().getCredentialId().getBase64Url())
              .type("public-key")
              .nickName(cred.getCredentialNickname().get())
              .registrationTime(cred.getRegistrationTime().atOffset(ZoneOffset.UTC))
              .lastUsedTime(cred.getLastUsedTime().atOffset(ZoneOffset.UTC))
              .build())
          .collect(Collectors.toList());

      return new UserCredentialsResponse().credentials(credList);
    } catch (Exception e) {
      System.out.println(e.getMessage());
      throw new Exception(e.getMessage());
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

    if (request.getUserVerification() == null) {
      authSelectBuilder.userVerification(UserVerificationRequirement.PREFERRED);
    } else {
      String uvReq = request.getUserVerification().getValue().toUpperCase();
      authSelectBuilder.userVerification(UserVerificationRequirement.valueOf(uvReq));
    }

    /*
     * Set the resident key requirement, set to preferred if not present
     */
    if (request.getResidentKey() == null) {
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
      } else if (aaReq.equals("cross-platform")) {
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
        .build();
  }

}
