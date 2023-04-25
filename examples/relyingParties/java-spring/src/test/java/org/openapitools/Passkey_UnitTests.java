package org.openapitools;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yubico.webauthn.data.AuthenticatorAssertionResponse;
import com.yubico.webauthn.data.AuthenticatorAttachment;
import com.yubico.webauthn.data.AuthenticatorAttestationResponse;
import com.yubico.webauthn.data.ByteArray;
import com.yubico.webauthn.data.ClientAssertionExtensionOutputs;
import com.yubico.webauthn.data.ClientRegistrationExtensionOutputs;
import com.yubico.webauthn.data.PublicKeyCredential;
import com.yubico.webauthn.data.PublicKeyCredentialCreationOptions;
import com.yubico.webauthn.data.PublicKeyCredentialRequestOptions;
import com.yubicolabs.passkey_rp.models.api.AssertionOptionsRequest;
import com.yubicolabs.passkey_rp.models.api.AssertionOptionsResponse;
import com.yubicolabs.passkey_rp.models.api.AssertionResultRequest;
import com.yubicolabs.passkey_rp.models.api.AssertionResultResponse;
import com.yubicolabs.passkey_rp.models.api.AttestationOptionsRequest;
import com.yubicolabs.passkey_rp.models.api.AttestationOptionsResponse;
import com.yubicolabs.passkey_rp.models.api.AttestationResultRequest;
import com.yubicolabs.passkey_rp.models.api.AttestationResultResponse;
import com.yubicolabs.passkey_rp.models.api.UserCredentialDelete;
import com.yubicolabs.passkey_rp.models.api.UserCredentialDeleteResponse;
import com.yubicolabs.passkey_rp.services.passkey.PasskeyOperations;
import com.yubicolabs.passkey_rp.services.passkey.RelyingPartyInstance;

import de.adesso.softauthn.Authenticator;
import de.adesso.softauthn.CredentialsContainer;
import de.adesso.softauthn.Origin;
import de.adesso.softauthn.authenticator.WebAuthnAuthenticator;
import lombok.var;

@SpringBootTest
@ActiveProfiles("test")
class Passkey_UnitTests {

  // Removing test as it requires a live database connection
  @Autowired
  RelyingPartyInstance relyingPartyInstance;

  @Autowired
  Environment env;

  @Autowired
  PasskeyOperations passkeyOperations;

  ObjectMapper mapper = new ObjectMapper();

  /**
   * ##################################################
   * RP CONFIGURATION
   * ##################################################
   * 
   * Ensure the relying party can be configured using the provided configurations
   */
  @Test
  void rpInstanceConfiguration() {
    /**
     * Test the RP_ID and RP_NAME
     */
    assertEquals(env.getProperty("RP_ID"), relyingPartyInstance.getRelyingParty().getIdentity().getId());
    assertEquals(env.getProperty("RP_NAME"),
        relyingPartyInstance.getRelyingParty().getIdentity().getName());
  }

  /**
   * ##################################################
   * ATTESTATION OPTIONS
   * ##################################################
   * 
   * This section contains the different test cases for the attestation options
   * method
   */

  /**
   * Control test
   * 
   * Ensure that the attestation options method triggers, and returns the expected
   * object
   */

  @Test
  void attestationOptions_Control() {
    /**
     * Check that the encoding method is working properly
     * JSON -> AttestationOptionsRequest
     */
    AttestationOptionsRequest request = attestationOptionsRequestGenerator(
        "attestation_options_1@test.com",
        "attestation_options_1",
        "direct",
        "preferred",
        "preferred",
        "");
    assertInstanceOf(AttestationOptionsRequest.class, request);
    try {
      AttestationOptionsResponse response = passkeyOperations.attestationOptions(request);
      assertInstanceOf(AttestationOptionsResponse.class, response);

      /**
       * Ensure the correct RP ID is conveyed
       */
      assertEquals(env.getProperty("RP_ID"), response.getPublicKey().getRp().getId());
    } catch (Exception e) {
      fail("@attestationOptions_Control failed: " + e.getMessage());
    }
  }

  /**
   * Test user attributes
   * 
   * Ensure that user attributes, userName, and displayName are processed
   * correctly
   * 
   * Ensure the correct error response is provided if either field is excluded
   */

  @Test
  void attestationOptions_UserAttributes() {
    String username = "assertion_options2@acme.com";
    String displayName = "assertion_options2";

    /**
     * Test to ensure that the method populated the PublicKey object with the
     * username
     * and displayname
     */
    try {

      AttestationOptionsRequest request = attestationOptionsRequestGenerator(
          username,
          displayName,
          "direct",
          "preferred",
          "preferred",
          "");

      AttestationOptionsResponse response = passkeyOperations.attestationOptions(request);

      assertEquals(username, response.getPublicKey().getUser().getName());
      assertEquals(displayName, response.getPublicKey().getUser().getDisplayName());
    } catch (Exception e) {
      fail("@attestationOptions_UserAttributes failed: " + e.getMessage());
    }

    /**
     * Test to ensure there was a failure when username was not provided
     */
    AttestationOptionsRequest request_noUsername = attestationOptionsRequestGenerator(
        null,
        displayName,
        "direct",
        "preferred",
        "preferred",
        "");

    assertThrows(Exception.class, () -> passkeyOperations.attestationOptions(request_noUsername));

    /**
     * Test to ensure there was a failure when displayname was not provided
     */
    AttestationOptionsRequest request_noDisplayname = attestationOptionsRequestGenerator(
        username,
        null,
        "direct",
        "preferred",
        "preferred",
        "");

    assertThrows(Exception.class, () -> passkeyOperations.attestationOptions(request_noDisplayname));
  }

  /**
   * Test residentKey requirements
   * 
   * Generate 6 different requests to test the different permutations of the
   * residentKey field
   * discouraged, preferred, required, field not included, none, non-valid enum
   */
  @Test
  void attestationOptions_ResidentKey() {
    try {
      AttestationOptionsRequest request_rkDiscouraged = attestationOptionsRequestGenerator(
          "attestation_options3@test.com",
          "attestation_options3",
          "direct",
          "discouraged",
          "preferred",
          "");

      AttestationOptionsResponse response_rkDiscouraged = passkeyOperations
          .attestationOptions(request_rkDiscouraged);

      assertEquals("discouraged",
          response_rkDiscouraged.getPublicKey().getAuthenticatorSelection()
              .getResidentKey().getValue());

      AttestationOptionsRequest request_rkPreferred = attestationOptionsRequestGenerator(
          "attestation_options3@test.com",
          "attestation_options3",
          "direct",
          "preferred",
          "preferred",
          "");

      AttestationOptionsResponse response_rkPreferred = passkeyOperations
          .attestationOptions(request_rkPreferred);

      assertEquals("preferred",
          response_rkPreferred.getPublicKey().getAuthenticatorSelection().getResidentKey()
              .getValue());

      AttestationOptionsRequest request_rkRequired = attestationOptionsRequestGenerator(
          "attestation_options3@test.com",
          "attestation_options3",
          "direct",
          "required",
          "preferred",
          "");

      AttestationOptionsResponse response_rkRequired = passkeyOperations
          .attestationOptions(request_rkRequired);

      assertEquals("required",
          response_rkRequired.getPublicKey().getAuthenticatorSelection().getResidentKey()
              .getValue());

      AttestationOptionsRequest request_rkEmpty = attestationOptionsRequestGenerator(
          "attestation_options3@test.com",
          "attestation_options3",
          "direct",
          "",
          "preferred",
          "");

      AttestationOptionsResponse response_rkEmpty = passkeyOperations
          .attestationOptions(request_rkEmpty);

      assertEquals("preferred",
          response_rkEmpty.getPublicKey().getAuthenticatorSelection().getResidentKey()
              .getValue());

      AttestationOptionsRequest request_rkNull = attestationOptionsRequestGenerator(
          "attestation_options3@test.com",
          "attestation_options3",
          "direct",
          null,
          "preferred",
          "");

      AttestationOptionsResponse response_rkNull = passkeyOperations
          .attestationOptions(request_rkNull);

      assertEquals("preferred",
          response_rkNull.getPublicKey().getAuthenticatorSelection().getResidentKey()
              .getValue());

      /**
       * Ensure that the encoder does not allow a user to submit a request without a
       * valid
       * enum of residentKey
       */
      assertThrows(Exception.class, () -> attestationOptionsRequestGenerator(
          "attestation_options3@test.com",
          "attestation_options3",
          "direct",
          "test_string",
          "preferred",
          ""));

    } catch (Exception e) {
      fail("@attestationOptions_ResidentKey failed: " + e.getMessage());
    }
  }

  /**
   * Test authenticatorAttachment requirements
   * 
   * Generate 4 different requests to test the different permutations of the
   * residentKey field
   * cross-platform, platform, field not included, field is null
   */
  @Test
  void attestationOptions_AuthAttachment() {
    try {
      AttestationOptionsRequest request_aaCrossPlatform = attestationOptionsRequestGenerator(
          "attestation_options4@test.com",
          "attestation_options4",
          "direct",
          "preferred",
          "preferred",
          "cross-platform");

      AttestationOptionsResponse response_aaCrossPlatform = passkeyOperations
          .attestationOptions(request_aaCrossPlatform);

      assertEquals("cross-platform",
          response_aaCrossPlatform.getPublicKey().getAuthenticatorSelection()
              .getAuthenticatorAttachment()
              .getValue());

      AttestationOptionsRequest request_aaPlatform = attestationOptionsRequestGenerator(
          "attestation_options4@test.com",
          "attestation_options4",
          "direct",
          "preferred",
          "preferred",
          "platform");

      AttestationOptionsResponse response_aaPlatform = passkeyOperations
          .attestationOptions(request_aaPlatform);

      assertEquals("platform",
          response_aaPlatform.getPublicKey().getAuthenticatorSelection()
              .getAuthenticatorAttachment()
              .getValue());

      AttestationOptionsRequest request_aaEmpty = attestationOptionsRequestGenerator(
          "attestation_options4@test.com",
          "attestation_options4",
          "direct",
          "preferred",
          "preferred",
          "");

      AttestationOptionsResponse response_aaEmpty = passkeyOperations
          .attestationOptions(request_aaEmpty);

      assertEquals(null,
          response_aaEmpty.getPublicKey().getAuthenticatorSelection()
              .getAuthenticatorAttachment());

      AttestationOptionsRequest request_aaNull = attestationOptionsRequestGenerator(
          "attestation_options4@test.com",
          "attestation_options4",
          "direct",
          "preferred",
          "preferred",
          null);

      AttestationOptionsResponse response_aaNull = passkeyOperations
          .attestationOptions(request_aaNull);

      assertEquals(null,
          response_aaNull.getPublicKey().getAuthenticatorSelection()
              .getAuthenticatorAttachment());

      /**
       * Ensure that the encoder does not allow a user to submit a request without a
       * valid
       * enum of residentKey
       */
      assertThrows(Exception.class, () -> attestationOptionsRequestGenerator(
          "attestation_options4@test.com",
          "attestation_options4",
          "direct",
          "preferred",
          "preferred",
          "test_string"));

    } catch (Exception e) {
      fail("@attestationOptions_AuthAttachment failed: " + e.getMessage());
    }
  }

  /**
   * Test userVerification requirements
   * 
   * Generate 6 different requests to test the different permutations of the
   * residentKey field
   * discouraged, preferred, required, field not included, none, non-valid enum
   */
  @Test
  void attestationOptions_UV() {
    try {
      AttestationOptionsRequest request_uvDiscouraged = attestationOptionsRequestGenerator(
          "attestation_options5@test.com",
          "attestation_options5",
          "direct",
          "preferred",
          "discouraged",
          "");

      AttestationOptionsResponse response_uvDiscouraged = passkeyOperations
          .attestationOptions(request_uvDiscouraged);

      assertEquals("discouraged",
          response_uvDiscouraged.getPublicKey().getAuthenticatorSelection()
              .getUserVerification().getValue());

      AttestationOptionsRequest request_uvPreferred = attestationOptionsRequestGenerator(
          "attestation_options5@test.com",
          "attestation_options5",
          "direct",
          "preferred",
          "preferred",
          "");

      AttestationOptionsResponse response_uvPreferred = passkeyOperations
          .attestationOptions(request_uvPreferred);

      assertEquals("preferred",
          response_uvPreferred.getPublicKey().getAuthenticatorSelection()
              .getUserVerification().getValue());

      AttestationOptionsRequest request_uvRequired = attestationOptionsRequestGenerator(
          "attestation_options5@test.com",
          "attestation_options5",
          "direct",
          "preferred",
          "required",
          "");

      AttestationOptionsResponse response_uvRequired = passkeyOperations
          .attestationOptions(request_uvRequired);

      assertEquals("required",
          response_uvRequired.getPublicKey().getAuthenticatorSelection()
              .getUserVerification().getValue());

      AttestationOptionsRequest request_uvEmpty = attestationOptionsRequestGenerator(
          "attestation_options5@test.com",
          "attestation_options5",
          "direct",
          "preferred",
          "",
          "");

      AttestationOptionsResponse response_uvEmpty = passkeyOperations
          .attestationOptions(request_uvEmpty);

      assertEquals("preferred",
          response_uvEmpty.getPublicKey().getAuthenticatorSelection()
              .getUserVerification().getValue());

      AttestationOptionsRequest request_uvNull = attestationOptionsRequestGenerator(
          "attestation_options5@test.com",
          "attestation_options5",
          "direct",
          "preferred",
          null,
          "");

      AttestationOptionsResponse response_uvNull = passkeyOperations
          .attestationOptions(request_uvNull);

      assertEquals("preferred",
          response_uvNull.getPublicKey().getAuthenticatorSelection().getUserVerification()
              .getValue());

      /**
       * Ensure that the encoder does not allow a user to submit a request without a
       * valid
       * enum of residentKey
       */
      assertThrows(Exception.class, () -> attestationOptionsRequestGenerator(
          "attestation_options5@test.com",
          "attestation_options5",
          "direct",
          "preferred",
          "test_string",
          ""));

    } catch (Exception e) {
      fail("@attestationOptions_UV failed: " + e.getMessage());
    }
  }

  /**
   * ##################################################
   * ATTESTATION RESULT
   * ##################################################
   * 
   * This section contains the different test cases for ensuring that
   * a new credential can be registered
   */

  /**
   * Control test for credential registration
   * 
   * Ensure that a credential, created with valid attestation options, can be
   * registered to the RP
   */
  @Test
  void attestationResult_Control() {
    CredentialsContainer credentials = createCredentialContainer();

    AttestationOptionsRequest attestationOptionsRequest = attestationOptionsRequestGenerator(
        "attestation_result1@test.com",
        "attestation_result1",
        "direct",
        "preferred",
        "preferred",
        "");

    try {
      AttestationOptionsResponse attestationOptionsResponse = passkeyOperations
          .attestationOptions(attestationOptionsRequest);
      String jsonString = mapper.writeValueAsString(attestationOptionsResponse.getPublicKey());

      PublicKeyCredentialCreationOptions options = PublicKeyCredentialCreationOptions.fromJson(jsonString);

      /**
       * Attempt to create a new credential using the softauthn Java library
       * https://github.com/adessoSE/softauthn
       */
      PublicKeyCredential<AuthenticatorAttestationResponse, ClientRegistrationExtensionOutputs> pkc = credentials
          .create(options);

      /**
       * Convert the requestId and the newly created credential to JSON then to an
       * AttestationResultRequest
       * This is meant to mimic the real world where a credential will be returned
       * from a `create` call as a JSON object, then encoded by the ObjectMapper used
       * by our API
       */
      AttestationResultRequest attestationResult = buildAttestationResultResponse(
          attestationOptionsResponse.getRequestId(), pkc);

      AttestationResultResponse attestationResultResponse = passkeyOperations
          .attestationResult(attestationResult);

      assertEquals("created", attestationResultResponse.getStatus());
    } catch (Exception e) {
      fail("@attestationResult_Control failed: " + e.getMessage());
    }
  }

  /**
   * Inactive attestation request
   * 
   * Ensure that a credential cannot be registered if an attestation request has
   * already been used / marked as inactive
   */

  @Test
  void attestationResult_inactiveRequest() {
    /**
     * Create a valid credential
     */
    CredentialsContainer credentials = createCredentialContainer();

    AttestationOptionsRequest attestationOptionsRequest = attestationOptionsRequestGenerator(
        "attestation_result_2",
        "attestation_result_1",
        "direct",
        "preferred",
        "preferred",
        "");

    try {
      /**
       * Preserve this request, as you will use it to generate a second credential
       */
      AttestationOptionsResponse response = passkeyOperations.attestationOptions(attestationOptionsRequest);
      String jsonString = mapper.writeValueAsString(response.getPublicKey());
      PublicKeyCredentialCreationOptions options = PublicKeyCredentialCreationOptions.fromJson(jsonString);

      /**
       * Generate the first credential
       */
      PublicKeyCredential<AuthenticatorAttestationResponse, ClientRegistrationExtensionOutputs> pkc = credentials
          .create(options);

      /**
       * Assert failure for a valid credential if a request ID is not present
       */

      AttestationResultRequest attestationResult_invalidID = buildAttestationResultResponse(
          ByteArray.fromBase64Url("randomString").getBase64Url(),
          pkc);
      assertThrows(Exception.class, () -> passkeyOperations.attestationResult(attestationResult_invalidID));

      /*
       * Successfully register a valid passkey
       */
      AttestationResultRequest attestationResult = buildAttestationResultResponse(response.getRequestId(), pkc);
      passkeyOperations.attestationResult(attestationResult);

      /**
       * Attempt to generate the second credential
       */
      PublicKeyCredential<AuthenticatorAttestationResponse, ClientRegistrationExtensionOutputs> pkc_invalid = credentials
          .create(options);
      AttestationResultRequest attestationResult_invalid = buildAttestationResultResponse(response.getRequestId(),
          pkc_invalid);
      assertThrows(Exception.class, () -> passkeyOperations.attestationResult(attestationResult_invalid));

    } catch (Exception e) {
      fail("@attestationResult_inactiveRequest failed: " + e.getMessage());
    }
  }

  /**
   * Ensure that a credential cannot register if it mimics a requestID, but
   * without the correct attestation options
   */
  @Test
  void attestationResult_nonMatchingOptions() {
    CredentialsContainer credentials = createCredentialContainer();

    /**
     * We will use these options to generate a credential
     */
    AttestationOptionsRequest attestationOptionsRequest = attestationOptionsRequestGenerator(
        "attestation_result_3",
        "attestation_result_3",
        "direct",
        "preferred",
        "preferred",
        "");

    /**
     * We will use the ID of this request
     */
    AttestationOptionsRequest attestationOptionsRequest_spoof = attestationOptionsRequestGenerator(
        "attestation_result_3",
        "attestation_result_3",
        "direct",
        "preferred",
        "preferred",
        "platform");

    try {
      /**
       * Preserve this request, as you will use it to generate a second credential
       */
      AttestationOptionsResponse attestationOptionsResponse = passkeyOperations
          .attestationOptions(attestationOptionsRequest);
      AttestationOptionsResponse attestationOptionsResponse_spoof = passkeyOperations
          .attestationOptions(attestationOptionsRequest_spoof);
      String jsonString = mapper.writeValueAsString(attestationOptionsResponse.getPublicKey());

      PublicKeyCredentialCreationOptions options = PublicKeyCredentialCreationOptions.fromJson(jsonString);

      /**
       * Generate the first credential
       */
      PublicKeyCredential<AuthenticatorAttestationResponse, ClientRegistrationExtensionOutputs> pkc = credentials
          .create(options);

      AttestationResultRequest attestationResult = buildAttestationResultResponse(
          attestationOptionsResponse_spoof.getRequestId(),
          pkc);
      assertThrows(Exception.class, () -> passkeyOperations.attestationResult(attestationResult));

    } catch (Exception e) {
      fail("@attestationResult_nonMatchingOptions failed: " + e.getMessage());
    }
  }

  /**
   * ##################################################
   * ASSERTION OPTIONS
   * ##################################################
   * 
   * This section contains the different test cases for generating assertion
   * options
   */

  /**
   * Discoverable credentials
   * Ensure that assertion options can be generated for discoverable credential
   * flows
   * 
   * No credential creation needed, as the service should not populate the
   * `allowCredentials` list
   * 
   * We will also ensure that even when a credential is added to this user, that
   * the list will remain null
   */

  @Test
  void assertionOptions_DiscoverableCredentials() {
    try {
      /**
       * Discoverable credential flow, so we will pass an empty username
       */
      AssertionOptionsRequest assertionOptionsRequest = assertionOptionsRequestGenerator("");

      AssertionOptionsResponse assertionOptionsResponse = passkeyOperations.assertionOptions(assertionOptionsRequest);
      assertInstanceOf(AssertionOptionsResponse.class, assertionOptionsResponse);

      /**
       * Ensure the correct RP ID is conveyed
       */
      assertEquals(env.getProperty("RP_ID"), assertionOptionsResponse.getPublicKey().getRpId());

      /**
       * Ensure the allowCredentials list is not present
       */
      assertEquals(null, assertionOptionsResponse.getPublicKey().getAllowCredentials());

    } catch (Exception e) {
      fail("@assertionOptions_DiscoverableCredentials failed: " + e.getMessage());
    }
  }

  /**
   * Non-discoverable credentials
   * Ensure that assertion options can be generated for non-discoverable
   * credential flows
   * 
   * Need to register a credential first in order to validate that the
   * `allowCredentials` list is populated with the credential ID of the created
   * credential
   */

  @Test
  void assertionOptions_NonDiscoverableCredentials() {
    try {
      CredentialsContainer credentials = createCredentialContainer();

      /**
       * Register a credential for the user
       * 
       * Need to ensure that the assertionOptions contains an allowCredentials list
       * populated with a credential
       */
      AttestationOptionsRequest attestationOptionsRequest = attestationOptionsRequestGenerator(
          "assertion_options1@test.com",
          "assertion_options1",
          "direct",
          "preferred",
          "preferred",
          "");
      AttestationOptionsResponse attestationOptionsResponse = passkeyOperations
          .attestationOptions(attestationOptionsRequest);
      String jsonString = mapper.writeValueAsString(attestationOptionsResponse.getPublicKey());
      PublicKeyCredentialCreationOptions options = PublicKeyCredentialCreationOptions.fromJson(jsonString);
      PublicKeyCredential<AuthenticatorAttestationResponse, ClientRegistrationExtensionOutputs> pkc = credentials
          .create(options);
      AttestationResultRequest attestationResult = buildAttestationResultResponse(
          attestationOptionsResponse.getRequestId(), pkc);
      AttestationResultResponse attestationResultResponse = passkeyOperations
          .attestationResult(attestationResult);

      /**
       * Ensure the credential was created
       */
      if (!attestationResultResponse.getStatus().equals("created")) {
        /**
         * Credential was not created, return error
         */
        fail("@assertionOptions_NonDiscoverableCredentials failed: The credential could not be created");
      }

      /**
       * Create assertionOptions for the specified user
       */
      AssertionOptionsRequest assertionOptionsRequest = assertionOptionsRequestGenerator("assertion_options1@test.com");

      AssertionOptionsResponse assertionOptionsResponse = passkeyOperations.assertionOptions(assertionOptionsRequest);
      assertInstanceOf(AssertionOptionsResponse.class, assertionOptionsResponse);

      /**
       * Ensure the correct RP ID is conveyed
       */
      /**
       * Ensure the correct RP ID is conveyed
       */
      assertEquals(env.getProperty("RP_ID"), assertionOptionsResponse.getPublicKey().getRpId());

      /**
       * Ensure the allowCredentials list contains one entry
       */
      assertEquals(1, assertionOptionsResponse.getPublicKey().getAllowCredentials().size());
    } catch (Exception e) {
      fail("@assertionOptions_NonDiscoverableCredentials failed: " + e.getMessage());
    }
  }

  /**
   * ##################################################
   * ASSERTION RESULT
   * ##################################################
   * 
   * This section contains the different test cases for ensuring that a passkey
   * can be used to authenticate
   */

  /**
   * Control - Discoverable credential
   * 
   * Ensure that a user, with a registered passkey, can authenticate into an
   * account using a discoverable credential flow
   */
  @Test
  void assertionResult_DiscoverableCredentials() {
    try {
      CredentialsContainer credentials = createCredentialContainer();

      /**
       * Register a credential for the user
       */
      AttestationOptionsRequest attestationOptionsRequest = attestationOptionsRequestGenerator(
          "assertion_result1@text.com",
          "assertion_result1",
          "direct",
          "preferred",
          "preferred",
          "");
      AttestationOptionsResponse response = passkeyOperations.attestationOptions(attestationOptionsRequest);
      String jsonString = mapper.writeValueAsString(response.getPublicKey());
      PublicKeyCredentialCreationOptions options = PublicKeyCredentialCreationOptions.fromJson(jsonString);
      PublicKeyCredential<AuthenticatorAttestationResponse, ClientRegistrationExtensionOutputs> pkc = credentials
          .create(options);
      AttestationResultRequest attestationResult = buildAttestationResultResponse(response.getRequestId(), pkc);
      AttestationResultResponse attestationResultResponse = passkeyOperations
          .attestationResult(attestationResult);

      if (!attestationResultResponse.getStatus().equals("created")) {
        /**
         * Credential was not created, return error
         */
        fail("@assertionResult_DiscoverableCredentials failed: The credential could not be created");
      }

      /**
       * Create assertionOptions for the specified user
       * 
       * Empty username for the discoverable credential flow
       */
      AssertionOptionsRequest assertionOptionsRequest = assertionOptionsRequestGenerator("");
      AssertionOptionsResponse assertionOptionsResponse = passkeyOperations.assertionOptions(assertionOptionsRequest);

      /**
       * Perform a mock get ceremony to authenticate using the softauthn Java library
       * https://github.com/adessoSE/softauthn
       */

      String assertionOptionsResponse_json = mapper.writeValueAsString(assertionOptionsResponse.getPublicKey());
      PublicKeyCredentialRequestOptions pkc_assertionOptions = assertionOptionstoPKC(assertionOptionsResponse_json);

      /**
       * assertionResult provided by the WebAuthn GET ceremony
       */
      PublicKeyCredential<AuthenticatorAssertionResponse, ClientAssertionExtensionOutputs> credential = credentials
          .get(pkc_assertionOptions);

      /**
       * Package the assertionResult, and requestID into a format that can be used by
       * our RP
       * 
       * This will mimic the JSON encoded string that will be sent by our client
       */
      AssertionResultRequest assertionResultRequest = buildAssertionResultResponse(
          assertionOptionsResponse.getRequestId(), credential);

      AssertionResultResponse assertionResultResponse = passkeyOperations.assertionResponse(assertionResultRequest);

      /**
       * Ensure the status property returned by the API denotes "ok"
       * 
       * This means that a successful auth ceremony was completed
       */
      assertTrue(assertionResultResponse.getStatus().equals("ok"));

    } catch (Exception e) {
      fail("@assertionResult_DiscoverableCredentials failed: " + e.getMessage());
    }
  }

  /**
   * Control - Non-discoverable credential
   * 
   * Ensure that a user, with a registered passkey, can authenticate into an
   * account using a non-discoverable credential flow
   */
  @Test
  void assertionResult_NonDiscoverableCredentials() {
    try {
      CredentialsContainer credentials = createCredentialContainer();

      /**
       * Register a credential for the user
       */
      AttestationOptionsRequest attestationOptionsRequest = attestationOptionsRequestGenerator(
          "assertion_result2@test.com",
          "assertion_result2",
          "direct",
          "preferred",
          "preferred",
          "");
      AttestationOptionsResponse response = passkeyOperations.attestationOptions(attestationOptionsRequest);
      String jsonString = mapper.writeValueAsString(response.getPublicKey());
      PublicKeyCredentialCreationOptions options = PublicKeyCredentialCreationOptions.fromJson(jsonString);
      PublicKeyCredential<AuthenticatorAttestationResponse, ClientRegistrationExtensionOutputs> pkc = credentials
          .create(options);
      AttestationResultRequest attestationResult = buildAttestationResultResponse(response.getRequestId(), pkc);
      AttestationResultResponse attestationResultResponse = passkeyOperations
          .attestationResult(attestationResult);

      if (!attestationResultResponse.getStatus().equals("created")) {
        /**
         * Credential was not created, return error
         */
        fail("@assertionResult_NonDiscoverableCredentials failed: The credential could not be created");
      }

      /**
       * Create assertionOptions for the specified user
       */

      AssertionOptionsRequest assertionOptionsRequest = assertionOptionsRequestGenerator("assertion_result2@test.com");
      AssertionOptionsResponse assertionOptionsResponse = passkeyOperations.assertionOptions(assertionOptionsRequest);

      /**
       * Perform a mock get ceremony to authenticate a user
       */
      String assertionOptionsResponse_json = mapper.writeValueAsString(assertionOptionsResponse.getPublicKey());
      PublicKeyCredentialRequestOptions options_assertion = assertionOptionstoPKC(assertionOptionsResponse_json);

      /**
       * assertionResult provided by the WebAuthn GET ceremony
       */
      PublicKeyCredential<AuthenticatorAssertionResponse, ClientAssertionExtensionOutputs> credential = credentials
          .get(options_assertion);

      /**
       * Package the assertionResult, and requestID into a format that can be used by
       * our RP
       * 
       * This will mimic the JSON encoded string that will be sent by our client
       */
      AssertionResultRequest assertionResultRequest = buildAssertionResultResponse(
          assertionOptionsResponse.getRequestId(), credential);

      AssertionResultResponse assertionResultResponse = passkeyOperations.assertionResponse(assertionResultRequest);

      /**
       * Ensure the status property returned by the API denotes "ok"
       * 
       * This means that a successful auth ceremony was completed
       */
      assertTrue(assertionResultResponse.getStatus().equals("ok"));

    } catch (Exception e) {
      fail("@assertionResult_NonDiscoverableCredentials failed: " + e.getMessage());
    }
  }

  /**
   * Passkey deleted
   * 
   * Ensure that a user cannot authenticate with a passkey once it has been
   * deleted from their account
   */
  @Test
  void assertionResult_Deleted() {
    try {
      CredentialsContainer credentials = createCredentialContainer();

      /**
       * Register a credential for the user
       */
      AttestationOptionsRequest attestationOptionsRequest = attestationOptionsRequestGenerator(
          "assertion_result3@test.com",
          "assertion_result3",
          "direct",
          "preferred",
          "preferred",
          "");
      AttestationOptionsResponse response = passkeyOperations.attestationOptions(attestationOptionsRequest);
      String jsonString = mapper.writeValueAsString(response.getPublicKey());
      PublicKeyCredentialCreationOptions options = PublicKeyCredentialCreationOptions.fromJson(jsonString);
      PublicKeyCredential<AuthenticatorAttestationResponse, ClientRegistrationExtensionOutputs> pkc = credentials
          .create(options);
      AttestationResultRequest attestationResult = buildAttestationResultResponse(response.getRequestId(), pkc);
      AttestationResultResponse attestationResultResponse = passkeyOperations
          .attestationResult(attestationResult);

      if (!attestationResultResponse.getStatus().equals("created")) {
        /**
         * Credential was not created, return error
         */
        fail("assertionResult_Deleted failed: The credential could not be completed");
      }

      /**
       * Ensure a successful auth request was processed
       */

      AssertionOptionsRequest assertionOptionsRequest = assertionOptionsRequestGenerator("assertion_result3@test.com");
      AssertionOptionsResponse assertionOptionsResponse = passkeyOperations.assertionOptions(assertionOptionsRequest);
      String assertionOptionsResponse_json = mapper.writeValueAsString(assertionOptionsResponse.getPublicKey());
      PublicKeyCredentialRequestOptions options_assertion = assertionOptionstoPKC(assertionOptionsResponse_json);
      PublicKeyCredential<AuthenticatorAssertionResponse, ClientAssertionExtensionOutputs> credential = credentials
          .get(options_assertion);
      AssertionResultRequest assertionResultRequest = buildAssertionResultResponse(
          assertionOptionsResponse.getRequestId(), credential);
      AssertionResultResponse assertionResultResponse = passkeyOperations.assertionResponse(assertionResultRequest);

      if (!assertionResultResponse.getStatus().equals("ok")) {
        fail("@assertionResult_Deleted failed: The initial auth ceremony could not be completed");
      }

      /**
       * Delete the credential
       * 
       * Will also test the ability of the delete method
       */
      UserCredentialDeleteResponse deleteResponse = passkeyOperations
          .deleteCredential(UserCredentialDelete.builder().id(credential.getId().getBase64Url()).build());
      assertTrue(deleteResponse.getResult().equals("deleted"));

      /**
       * Attempt to authenticate with the same credential
       */

      AssertionOptionsRequest assertionOptionsRequest_2 = assertionOptionsRequestGenerator(
          "assertion_result3@test.com");
      AssertionOptionsResponse assertionOptionsResponse_2 = passkeyOperations
          .assertionOptions(assertionOptionsRequest_2);
      String assertionOptionsResponse_json_2 = mapper.writeValueAsString(assertionOptionsResponse_2.getPublicKey());
      PublicKeyCredentialRequestOptions options_assertion_2 = assertionOptionstoPKC(assertionOptionsResponse_json_2);
      PublicKeyCredential<AuthenticatorAssertionResponse, ClientAssertionExtensionOutputs> credential_2 = credentials
          .get(options_assertion_2);
      AssertionResultRequest assertionResultRequest_2 = buildAssertionResultResponse(
          assertionOptionsResponse_2.getRequestId(), credential_2);
      assertThrows(Exception.class, () -> passkeyOperations.assertionResponse(assertionResultRequest_2));

    } catch (Exception e) {
      fail("@assertionResult_Deleted failed: " + e.getMessage());
    }
  }

  /**
   * ##################################################
   * HELPER METHODS
   * ##################################################
   * 
   * This section contains various helper methods that are used throughout the
   * test cases above
   */

  /**
   * Used to construct an AttestationOptionsRequest object to be processed by our
   * passkeyServices instance
   * 
   * This is meant to mimic a JSON encoded request that will be sent to the API
   */
  private AttestationOptionsRequest attestationOptionsRequestGenerator(
      String userName,
      String displayName,
      String attestation,
      String residentKey,
      String userVerification,
      String authenticatorAttachment) {
    ObjectNode rootNode = mapper.createObjectNode();

    ObjectNode authenticatorSelection = mapper.createObjectNode();
    authenticatorSelection.put("residentKey", residentKey);
    authenticatorSelection.put("userVerification", userVerification);
    authenticatorSelection.put("authenticatorAttachment", authenticatorAttachment);

    rootNode.set("authenticatorSelection", authenticatorSelection);

    rootNode.put("userName", userName);
    rootNode.put("displayName", displayName);
    rootNode.put("attestation", attestation);

    return mapper.convertValue(rootNode, AttestationOptionsRequest.class);
  }

  /**
   * Used to initialize a CredentialContainer from the softauthn library
   * https://github.com/adessoSE/softauthn
   * Acts as a virtual authenticator that mimics a real WebAuthn authenticator
   * 
   * @return object that can invoke create and get requests for unit testing
   */
  private CredentialsContainer createCredentialContainer() {
    var authenticator = WebAuthnAuthenticator.builder()
        .attachment(AuthenticatorAttachment.CROSS_PLATFORM)
        .supportClientSideDiscoverablePublicKeyCredentialSources(true)
        .supportUserVerification(true)
        .build();

    var origin = new Origin("http", "localhost", 3000, null);

    List<Authenticator> authList = new ArrayList<>();
    authList.add(authenticator);

    return new CredentialsContainer(origin, authList);
  }

  /**
   * Helper method to turn the new credential into an AttestationResultRequest
   * object to be processed by our API
   * 
   * @param requestId id of the attestation request
   * @param pkc       newly created credential, in a Java object format
   * @return requestid and pkc packaged as an AttestationResultRequest, from an
   *         encoded JSON string
   */
  private AttestationResultRequest buildAttestationResultResponse(String requestId,
      PublicKeyCredential<AuthenticatorAttestationResponse, ClientRegistrationExtensionOutputs> pkc) {
    ObjectNode rootNode = mapper.createObjectNode();

    rootNode.put("requestId", requestId);
    rootNode.set("makeCredentialResult", buildAttestationMakeCredentialResult(pkc));

    return mapper.convertValue(rootNode, AttestationResultRequest.class);

  }

  /**
   * Builds the inner layer of our AttestationResultRequest (makeCredentialResult
   * object)
   * Jackson has some issues encoding this as our makeCredentialResult object does
   * not make use of all the fields provided by the softauthn credential creation
   * 
   * @param pkc
   * @return
   */
  private ObjectNode buildAttestationMakeCredentialResult(
      PublicKeyCredential<AuthenticatorAttestationResponse, ClientRegistrationExtensionOutputs> pkc) {
    ObjectNode rootNode = mapper.createObjectNode();
    rootNode.put("id", pkc.getId().getBase64Url());
    rootNode.put("type", pkc.getType().getId());

    /**
     * Our project isn't taking advantage of the clientExtensionResults
     * We will default this to an empty object until we add it to our API schema for
     * use
     */
    ObjectNode emptyClientExtensionNode = mapper.createObjectNode();
    rootNode.set("clientExtensionResults", emptyClientExtensionNode);

    ObjectNode response = mapper.createObjectNode();
    response.put("attestationObject", pkc.getResponse().getAttestationObject().getBase64Url());
    response.put("clientDataJSON", pkc.getResponse().getClientDataJSON().getBase64Url());
    rootNode.set("response", response);

    return rootNode;

  }

  /**
   * Builds an AssertionOptionsRequest to be processed by the passkeyServices
   * instance
   * 
   * Meant to mimic a JSON encoded object that will be sent to the API
   */
  private AssertionOptionsRequest assertionOptionsRequestGenerator(
      String userName) {
    ObjectNode rootNode = mapper.createObjectNode();
    rootNode.put("userName", userName);

    return mapper.convertValue(rootNode, AssertionOptionsRequest.class);
  }

  /**
   * Need this method to add the extensions property to our PKC object
   * 
   * @param jsonString
   * @return
   */
  private PublicKeyCredentialRequestOptions assertionOptionstoPKC(
      String jsonString) throws Exception {

    try {
      ObjectNode rootNode = mapper.readValue(jsonString, ObjectNode.class);

      ObjectNode extensionRoot = mapper.createObjectNode();

      rootNode.set("extensions", extensionRoot);
      return mapper.convertValue(rootNode, PublicKeyCredentialRequestOptions.class);
    } catch (Exception e) {
      throw e;
    }

  }

  /**
   * Helper method to turn the new credential into an AttestationResultRequest
   * object to be processed by our API
   * 
   * @param requestId id of the attestation request
   * @param pkc       newly created credential, in a Java object format
   * @return requestid and pkc packaged as an AttestationResultRequest, from an
   *         encoded JSON string
   */
  private AssertionResultRequest buildAssertionResultResponse(String requestId,
      PublicKeyCredential<AuthenticatorAssertionResponse, ClientAssertionExtensionOutputs> pkc) {
    ObjectNode rootNode = mapper.createObjectNode();

    rootNode.put("requestId", requestId);
    rootNode.set("assertionResult", buildAssertionResultObject(pkc));

    return mapper.convertValue(rootNode, AssertionResultRequest.class);
  }

  /**
   * Builds the inner layer of our AttestationResultRequest (makeCredentialResult
   * object)
   * Jackson has some issues encoding this as our makeCredentialResult object does
   * not make use of all the fields provided by the softauthn credential creation
   * 
   * @param pkc
   * @return
   */
  private ObjectNode buildAssertionResultObject(
      PublicKeyCredential<AuthenticatorAssertionResponse, ClientAssertionExtensionOutputs> pkc) {
    ObjectNode rootNode = mapper.createObjectNode();
    rootNode.put("id", pkc.getId().getBase64Url());
    rootNode.put("type", pkc.getType().getId());

    /**
     * Our project isn't taking advantage of the clientExtensionResults
     * We will default this to an empty object until we add it to our API schema for
     * use
     */
    ObjectNode emptyClientExtensionNode = mapper.createObjectNode();
    rootNode.set("clientExtensionResults", emptyClientExtensionNode);

    ObjectNode response = mapper.createObjectNode();
    response.put("authenticatorData", pkc.getResponse().getAuthenticatorData().getBase64Url());
    response.put("clientDataJSON", pkc.getResponse().getClientDataJSON().getBase64Url());
    response.put("signature", pkc.getResponse().getSignature().getBase64Url());
    response.put("userHandle", pkc.getResponse().getUserHandle().get().getBase64Url());
    rootNode.set("response", response);

    return rootNode;

  }
}
