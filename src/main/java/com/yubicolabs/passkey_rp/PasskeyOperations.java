package com.yubicolabs.passkey_rp;

import java.security.SecureRandom;
import java.time.Clock;
import java.util.Collection;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.google.gson.JsonObject;
import com.yubico.webauthn.AssertionRequest;
import com.yubico.webauthn.AssertionResult;
import com.yubico.webauthn.FinishAssertionOptions;
import com.yubico.webauthn.FinishRegistrationOptions;
import com.yubico.webauthn.RegisteredCredential;
import com.yubico.webauthn.RegistrationResult;
import com.yubico.webauthn.StartAssertionOptions;
import com.yubico.webauthn.StartRegistrationOptions;
import com.yubico.webauthn.data.AuthenticatorSelectionCriteria;
import com.yubico.webauthn.data.ByteArray;
import com.yubico.webauthn.data.PublicKeyCredentialCreationOptions;
import com.yubico.webauthn.data.ResidentKeyRequirement;
import com.yubico.webauthn.data.UserIdentity;
import com.yubico.webauthn.data.UserVerificationRequirement;
import com.yubicolabs.passkey_rp.data.AuthenticationRequest;
import com.yubicolabs.passkey_rp.data.AuthenticationResponse;
import com.yubicolabs.passkey_rp.data.CredentialRegistration;
import com.yubicolabs.passkey_rp.data.RegistrationRequest;
import com.yubicolabs.passkey_rp.data.RegistrationResponse;

@Service
@Scope("singleton")
public class PasskeyOperations {

  private static final SecureRandom random = new SecureRandom();
  private final Clock clock = Clock.systemDefaultZone();

  @Autowired
  RelyingPartyInstance relyingPartyInstance;

  /**
   * Send PublicKeyCredentialCreationOptions to the client application
   * 
   * @param controllerRequest JSON with two properties
   *                          username: denotes the user making the request
   *                          UID: Unique identifier used to denote the user
   * @return RegistrationRequest with the applicable
   *         PublicKeyCredentialCreationOptions, and requestID
   * @throws Exception
   */
  public RegistrationRequest startRegistration(JsonObject controllerRequest) throws Exception {
    String username = controllerRequest.get("username").getAsString();
    String uid = controllerRequest.get("uid").getAsString();

    ByteArray userID;

    try {
      userID = ByteArray.fromBase64Url(uid);
    } catch (Exception e) {
      throw new Exception(e.getMessage());
    }

    UserIdentity userIdentity = UserIdentity.builder()
        .name(username)
        .displayName(username)
        .id(userID)
        .build();

    PublicKeyCredentialCreationOptions pubKey = relyingPartyInstance.getRelyingParty()
        .startRegistration(StartRegistrationOptions.builder()
            .user(userIdentity)
            .authenticatorSelection(AuthenticatorSelectionCriteria.builder()
                .residentKey(ResidentKeyRequirement.PREFERRED)
                .userVerification(UserVerificationRequirement.PREFERRED)
                .build())
            .build());

    RegistrationRequest request = RegistrationRequest.builder()
        .requestId(generateRandomByteArray(32))
        .publicKeyCredentialCreationOptions(pubKey)
        .publicKey(pubKey.toCredentialsCreateJson())
        .isActive(true)
        .build();

    if (relyingPartyInstance.getStorageInstance().getRegistrationRequestStorage().insert(request)) {
      return request;
    } else {
      throw new Exception("Failed to add registration request to storage");
    }
  }

  /**
   * Add the newly created passkey to storage
   * 
   * @param response
   * @return
   * @throws Exception
   */
  public CredentialRegistration finishRegistration(RegistrationResponse response) throws Exception {
    // Check if there is a matching registration request
    Optional<RegistrationRequest> maybeRequest = relyingPartyInstance.getStorageInstance()
        .getRegistrationRequestStorage()
        .getIfPresent(response.getRequestId());
    RegistrationRequest request;
    if (maybeRequest.isPresent()) {
      request = maybeRequest.get();
    } else {
      throw new Exception("Registration request not present");
    }
    // Check if the registration request is still active
    if (!request.getIsActive()) {
      throw new Exception("The registration session is not active");
    } else {
      relyingPartyInstance.getStorageInstance().getRegistrationRequestStorage().invalidate(request.getRequestId());

      RegistrationResult newRegistration = relyingPartyInstance.getRelyingParty()
          .finishRegistration(FinishRegistrationOptions.builder()
              .request(request.getPublicKeyCredentialCreationOptions())
              .response(response.getMakeCredentialResponse())
              .build());

      CredentialRegistration credentialToStore = CredentialRegistration.builder()
          .signatureCounter(
              response.getMakeCredentialResponse().getResponse().getParsedAuthenticatorData().getSignatureCounter())
          .userIdentity(request.getPublicKeyCredentialCreationOptions().getUser())
          .credentialNickname(Optional.of("Default name"))
          .registrationTime(clock.instant())
          .lastUpdateTime(clock.instant())
          .lastUsedTime(clock.instant())
          .credential(RegisteredCredential.builder()
              .credentialId(newRegistration.getKeyId().getId())
              .userHandle(request.getPublicKeyCredentialCreationOptions().getUser().getId())
              .publicKeyCose(newRegistration.getPublicKeyCose())
              .signatureCount(
                  response.getMakeCredentialResponse().getResponse().getParsedAuthenticatorData().getSignatureCounter())
              .build())
          .registrationRequest(request)
          .build();
      if (relyingPartyInstance.getStorageInstance().getCredentialStorage()
          .addRegistration(credentialToStore.getUserIdentity().getName(), credentialToStore)) {
        return credentialToStore;
      } else {
        throw new Exception("There was an issue adding the registration");
      }
    }

  }

  /**
   * Send PublicKeyCredentialCreationOptions to the client application
   * 
   * @return RegistrationRequest with the applicable
   *         PublicKeyCredentialCreationOptions, and requestID
   * @throws Exception
   */
  public AuthenticationRequest startAuthentication() throws Exception {
    AssertionRequest pubKey = relyingPartyInstance.getRelyingParty().startAssertion(StartAssertionOptions.builder()
        .userVerification(UserVerificationRequirement.PREFERRED)
        .build());

    AuthenticationRequest request = AuthenticationRequest.builder()
        .publicKeyCredentialRequestOptions(pubKey.getPublicKeyCredentialRequestOptions())
        .requestId(generateRandomByteArray(32))
        .publicKey(pubKey.toCredentialsGetJson())
        .isActive(true)
        .build();

    if (relyingPartyInstance.getStorageInstance().getAssertionRequestStorage().insert(request)) {
      return request;
    } else {
      throw new Exception("Assertion request failed");
    }
  }

  public AssertionResult finishAuthentication(AuthenticationResponse response) throws Exception {
    // Check if there is a matching authentication request
    Optional<AuthenticationRequest> maybeRequest = relyingPartyInstance.getStorageInstance()
        .getAssertionRequestStorage().getIfPresent(response.getRequestId());
    AuthenticationRequest request;
    if (maybeRequest.isPresent()) {
      request = maybeRequest.get();
    } else {
      throw new Exception("Authentication request not present");
    }

    // Check if the auth request is still active
    if (!request.getIsActive()) {
      throw new Exception("The auth session is not active");
    } else {
      relyingPartyInstance.getStorageInstance().getRegistrationRequestStorage().invalidate(request.getRequestId());

      AssertionResult newAssertion = relyingPartyInstance.getRelyingParty()
          .finishAssertion(FinishAssertionOptions.builder()
              .request(AssertionRequest.builder()
                  .publicKeyCredentialRequestOptions(request.getPublicKeyCredentialRequestOptions())
                  .build())
              .response(response.getAssertionResponse())
              .build());
      if (newAssertion.isSuccess()) {
        return newAssertion;
      } else {
        throw new Exception("Authentication failed, try again");
      }
    }
  }

  private static ByteArray generateRandomByteArray(int length) {
    byte[] bytes = new byte[length];
    random.nextBytes(bytes);
    return new ByteArray(bytes);
  }

  public Collection<RegistrationRequest> getAllRR() {
    return relyingPartyInstance.getStorageInstance().getRegistrationRequestStorage().getAll();
  }

  public Collection<CredentialRegistration> getAllCR() {
    return relyingPartyInstance.getStorageInstance().getCredentialStorage().getAll();
  }

}
