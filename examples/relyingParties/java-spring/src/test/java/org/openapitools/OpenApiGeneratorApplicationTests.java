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
import com.yubico.webauthn.data.AuthenticatorAttachment;
import com.yubico.webauthn.data.AuthenticatorAttestationResponse;
import com.yubico.webauthn.data.ClientRegistrationExtensionOutputs;
import com.yubico.webauthn.data.PublicKeyCredential;
import com.yubico.webauthn.data.PublicKeyCredentialCreationOptions;
import com.yubicolabs.passkey_rp.models.api.AttestationOptionsRequest;
import com.yubicolabs.passkey_rp.models.api.AttestationOptionsResponse;
import com.yubicolabs.passkey_rp.models.api.AttestationResultRequest;
import com.yubicolabs.passkey_rp.models.api.AttestationResultResponse;
import com.yubicolabs.passkey_rp.services.passkey.PasskeyOperations;
import com.yubicolabs.passkey_rp.services.passkey.RelyingPartyInstance;

import de.adesso.softauthn.Authenticator;
import de.adesso.softauthn.CredentialsContainer;
import de.adesso.softauthn.Origin;
import de.adesso.softauthn.authenticator.WebAuthnAuthenticator;
import lombok.var;

@SpringBootTest
@ActiveProfiles("test")
class OpenApiGeneratorApplicationTests {

  // Removing test as it requires a live database connection
  @Autowired
  RelyingPartyInstance relyingPartyInstance;

  @Autowired
  Environment env;

  @Autowired
  PasskeyOperations passkeyOperations;

  ObjectMapper mapper = new ObjectMapper();

  /**
   * Test the RelyingPartyInstance singleton parameters
   */
  @Test
  void testRPInstanceConfigs() {
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
   * Ensure that the method triggers, and returns the expected object
   */

  @Test
  void testAttestationOptions_Control() {
    /**
     * Check that the encoding method is working properly
     * JSON -> AttestationOptionsRequest
     */
    AttestationOptionsRequest request = attestationOptionsRequestGenerator(
        "csalas",
        "csalas",
        "direct",
        "preferred",
        "preferred",
        "");
    assertInstanceOf(AttestationOptionsRequest.class, request);
    try {
      AttestationOptionsResponse response = passkeyOperations.attestationOptions(request);
      assertInstanceOf(AttestationOptionsResponse.class, response);

    } catch (Exception e) {
      e.printStackTrace();
      System.out.println(e.getMessage());
      fail("Test failed to generate assertion options");
    }
  }

  /**
   * Test user attributes
   * Ensure that user attributes, userName, and displayName are processed
   * correctly
   * Ensure the correct error response is provided if either field is excluded
   */

  @Test
  void testAttestationOptions_UserAttributes() {
    String username = "myuser@acme.com";
    String displayName = "myuser";

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
      e.printStackTrace();
      System.out.println(e.getMessage());
      fail("Test failed to generate assertion options");
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
   * Generate 6 different requests to test the different permutations of the
   * residentKey field
   * discouraged, preferred, required, field not included, none, non-valid enum
   */
  @Test
  void testAttestationOptions_ResidentKey() {
    try {
      AttestationOptionsRequest request_rkDiscouraged = attestationOptionsRequestGenerator(
          "csalas",
          "csalas",
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
          "csalas",
          "csalas",
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
          "csalas",
          "csalas",
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
          "csalas",
          "csalas",
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
          "csalas",
          "csalas",
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
          "csalas",
          "csalas",
          "direct",
          "test_string",
          "preferred",
          ""));

    } catch (Exception e) {
      e.printStackTrace();
      System.out.println(e.getMessage());
      fail("Test failed to generate assertion options");
    }
  }

  /**
   * Test authenticatorAttachment requirements
   * Generate 3 different requests to test the different permutations of the
   * residentKey field
   * cross-platform, platform, field not included
   */
  @Test
  void testAttestationOptions_AuthAttachment() {
    try {
      AttestationOptionsRequest request_aaCrossPlatform = attestationOptionsRequestGenerator(
          "csalas",
          "csalas",
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
          "csalas",
          "csalas",
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
          "csalas",
          "csalas",
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
          "csalas",
          "csalas",
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
          "csalas",
          "csalas",
          "direct",
          "preferred",
          "preferred",
          "test_string"));

    } catch (Exception e) {
      e.printStackTrace();
      System.out.println(e.getMessage());
      fail("Test failed to generate assertion options");
    }
  }

  /**
   * Test userVerification requirements
   * Generate 6 different requests to test the different permutations of the
   * residentKey field
   * discouraged, preferred, required, field not included, none, non-valid enum
   */
  @Test
  void testAttestationOptions_UV() {
    try {
      AttestationOptionsRequest request_uvDiscouraged = attestationOptionsRequestGenerator(
          "csalas",
          "csalas",
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
          "csalas",
          "csalas",
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
          "csalas",
          "csalas",
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
          "csalas",
          "csalas",
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
          "csalas",
          "csalas",
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
          "csalas",
          "csalas",
          "direct",
          "preferred",
          "test_string",
          ""));

    } catch (Exception e) {
      e.printStackTrace();
      System.out.println(e.getMessage());
      fail("Test failed to generate assertion options");
    }
  }

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
   * ##################################################
   * ATTESTATION RESULT
   * ##################################################
   * 
   * This section contains the different test cases for ensuring that
   * a new credential can be registered
   */

  @Test
  void attestationResult_Control() {
    CredentialsContainer credentials = createCredentialContainer();

    AttestationOptionsRequest attestationOptionsRequest = attestationOptionsRequestGenerator(
        "csalas",
        "csalas",
        "direct",
        "preferred",
        "preferred",
        "");

    try {
      AttestationOptionsResponse response = passkeyOperations.attestationOptions(attestationOptionsRequest);
      String jsonString = mapper.writeValueAsString(response.getPublicKey());
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
       * from a `create`
       * call as a JSON object, then encoded by the ObjectMapper used by our API
       */
      AttestationResultRequest attestationResult = buildAttestationResultResponse(response.getRequestId(), pkc);

      AttestationResultResponse attestationResultResponse = passkeyOperations.attestationResult(attestationResult);

      assertEquals("created", attestationResultResponse.getStatus());
    } catch (Exception e) {
      e.printStackTrace();
      System.out.println(e.getMessage());
      fail("Failed to register the credential");
    }
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
}